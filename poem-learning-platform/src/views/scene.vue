<template>
    <div>
        <div id="app"></div>
        <div id="info">
            <div id="player-position"></div>
        </div>
    </div>
</template>

<script>
    import * as THREE from "three";
    import { OrbitControls } from "three/addons/controls/OrbitControls.js";
    import { World } from "../scripts/world.js";
    import Stats from "three/examples/jsm/libs/stats.module.js";
    import { createGUI } from "../scripts/ui.js";
    import { Player } from "../scripts/player.js";
    import { Physics } from "../scripts/physics.js";
    import { Screen } from "../scripts/screen.js";
    import loader from '../scripts/loader.js';
    import router from '../router/index.js'


    export default {
        name: "Scene",
        mounted() {
            const role = this.$route.params.role;
            const room = this.$route.params.room;

            const userInfoString = localStorage.getItem("userInfo");
            const userInfo = JSON.parse(userInfoString);
            const name = userInfo.name;
            const userId = userInfo.userId;
            const modelId = userInfo.modelId;

            let preNum = 0; // 记录准备状态的人数
            let topicNum = 0; // 记录已答的题目数
            let showTopic = false; // 是否显示题目
            let countdownTimer;
            let answer = 0;
            let queationId = 0;
            let showEnd = false; // 是否显示结束界面
            let sendTopicRequest = false; // 是否发送题目请求
            let opponentId;  // 对手的userId
            let winNum = 0; // 记录胜利次数
            let loseNum = 0; // 记录失败次数
            let isStill = true; // 是否静止

            // 显示性能监控（FPS）
            const stats = new Stats();
            document.body.appendChild(stats.dom);
    
            // Renderer
            const renderer = new THREE.WebGLRenderer( { antialias: true } );
            renderer.setPixelRatio(window.devicePixelRatio);
            renderer.setSize(window.innerWidth, window.innerHeight);
            renderer.setClearColor(0xb0e0ff);
            renderer.outputColorSpace = THREE.LinearSRGBColorSpace
            renderer.outputEncoding = THREE.sRGBEncoding;
            renderer.shadowMap.enabled = true;
            renderer.shadowMap.type = THREE.PCFSoftShadowMap;
            document.body.appendChild(renderer.domElement);
    
            // Camera
            const orbitCamera = new THREE.PerspectiveCamera(
                60,
                window.innerWidth / window.innerHeight,
            );
            orbitCamera.position.set(32, 15, 1);
            orbitCamera.lookAt(32, 15, 60);
            orbitCamera.controls = null;
            const orbitCameraHelper = new THREE.CameraHelper(orbitCamera);
    
            // // 实现鼠标的旋转、缩放、平移
            // const controls = new OrbitControls(orbitCamera, renderer.domElement);
            // controls.target.set(16, 0, 16);
            // controls.update();
    
            // Scene
            const scene = new THREE.Scene();
            const world = new World();
            world.generate();
            // 将生成的方块添加到场景中
            scene.add(world);
            // 添加雾
            scene.fog = new THREE.Fog(0xb0e0ff, 50, 80);
    
            // scene.add(orbitCameraHelper);
    
            const physics = new Physics(scene);

            let screen = new Screen(scene);
    
            const player = new Player(scene, screen, this.$route.params, name, modelId);
    
            const light = new THREE.DirectionalLight();
            // 添加光源，包括环境光、平行光
            function setupLight() {
                light.intensity = 1.5;
                light.castShadow = true;
                light.shadow.camera.top = 100;
                light.shadow.camera.bottom = -100;
                light.shadow.camera.left = -100;
                light.shadow.camera.right = 100;
                light.shadow.camera.near = 0.1;
                light.shadow.camera.far = 200;
                light.shadow.bias = -0.0005;
                light.shadow.mapSize = new THREE.Vector2(2048, 2048);
        
                scene.add(light);
                scene.add(light.target);
        
                // 添加一个辅助线，显示光源的方向
                // const shadowHelper = new THREE.CameraHelper(light.shadow.camera);
                // scene.add(shadowHelper);
        
                const ambientLight = new THREE.AmbientLight(0xaaaaaa, 3);
                scene.add(ambientLight);
            }
            setupLight();

            // 存储玩家 socketid和模型的映射
            const playerMap = new Map();
            const ws = new WebSocket(`ws://localhost:2345/ws?room=${room}`);
            let isSending = false; // 用于标记是否正在发送消息
            ws.onopen = function(event) {
                const loginObject = {
                    type: "login",
                    role: role, 
                    name: name,
                    userId: userId,
                    modelId: modelId,
                    room: room
                }
                ws.send(JSON.stringify(loginObject));                
            };
            ws.onmessage = function(event) {
                // console.log("Received message: ", event.data);
                const message = JSON.parse(event.data);
                if (message.type == "login") {
                    // 导入模型
                    loader.loadModelWithNumber(message.modelId, 1).then((mmd) => {
                        if (message.role == 0) {
                            mmd.mesh.position.set(32, 16, 60);
                        } else if (message.role == 1) {
                            mmd.mesh.position.set(17, 16, 60);
                            opponentId = message.userId;
                        } else if (message.role == 2) {
                            mmd.mesh.position.set(46, 16, 60);
                            opponentId = message.userId;
                        }
                        mmd.mesh.rotation.set(0, Math.PI, 0);
                        scene.add(mmd.mesh);
                        const boundingBox = new THREE.Box3().setFromObject(mmd.mesh);
                        const height = boundingBox.max.y - boundingBox.min.y;
                        playerMap.set(message.socketId, {model: mmd, modelStill: mmd,height: height});

                        loader.loadModelWithNumber(message.modelId, 2).then((mmd) => {
                            let data = playerMap.get(message.socketId);
                            if (data == -1) {
                                return;
                            }
                            data.modelWalk = mmd;
                            playerMap.set(message.socketId, data);
                        });
                    });
                    // 显示状态
                    screen.showState(scene, message.role, message.name, false);
                }
                else if (message.type == "position") {
                    const position = JSON.parse(message.position);
                    const rotation = JSON.parse(message.rotation);
                    
                    if (playerMap.get(message.socketId) == undefined) {
                        opponentId = message.userId;
                        playerMap.set(message.socketId, 0);
                        loader.loadModelWithNumber(message.modelId, 2).then((mmd) => {
                            const boundingBox = new THREE.Box3().setFromObject(mmd.mesh);
                            const height = boundingBox.max.y - boundingBox.min.y;

                            mmd.mesh.position.set(position.x, position.y - height, position.z);
                            mmd.mesh.rotation.set(rotation.x, rotation.y, rotation.z);
                            
                            scene.add(mmd.mesh);
                            playerMap.set(message.socketId, {model: mmd, modelWalk: mmd, height: height});

                            loader.loadModelWithNumber(message.modelId, 1).then((mmd) => {
                                let data = playerMap.get(message.socketId);
                                data.modelStill = mmd;
                                playerMap.set(message.socketId, data);
                            });
                        });
                        
                        if (!screen.isState[message.role - 1]) {
                            screen.showState(scene, message.role, message.name, false);
                        }
                    } else {
                        if (playerMap.get(message.socketId) != 0 && playerMap.get(message.socketId) != -1){
                            let modelWalk = playerMap.get(message.socketId).modelWalk;
                            if (modelWalk) {
                                modelWalk.mesh.position.set(position.x, position.y - playerMap.get(message.socketId).height, position.z);
                                modelWalk.mesh.rotation.set(rotation._x, rotation._y, rotation._z);
                                scene.remove(playerMap.get(message.socketId).model.mesh);
                                playerMap.get(message.socketId).model = modelWalk;
                                scene.add(modelWalk.mesh);
                            }
                        }
                    }
                }
                else if (message.type == "still") {
                    if (playerMap.get(message.socketId) != 0 && playerMap.get(message.socketId) != -1){
                        let modelStill = playerMap.get(message.socketId).modelStill;
                        if (modelStill) {
                            const mixer = modelStill.helper.objects.get(modelStill.mesh).mixer;
                            const AnimationAction = mixer.clipAction(modelStill.animation);
                            AnimationAction.time = 0.2;
                            AnimationAction.play();
                            modelStill.mesh.position.copy(playerMap.get(message.socketId).model.mesh.position);
                            modelStill.mesh.rotation.copy(playerMap.get(message.socketId).model.mesh.rotation);
                            scene.remove(playerMap.get(message.socketId).model.mesh);
                            playerMap.get(message.socketId).model = modelStill;
                            scene.add(modelStill.mesh);
                        }
                    }
                }
                else if (message.type == "pre") {
                    screen.showState(scene, message.role, message.name, true);
                    preNum++;
                }
                else if (message.type == "depre") {
                    screen.showState(scene, message.role, message.name, false);
                    preNum--;
                }
                else if (message.type == "topic") {
                    if (showTopic == false) {
                        topicNum++;
                        showTopic = true;
                        const topic = [message.question, ...message.options];
                        topic[0] = topicNum + ". " + topic[0];
                        topic[1] = "A. " + topic[1];
                        topic[2] = "B. " + topic[2];
                        topic[3] = "C. " + topic[3];
                        if (topic.length > 4) {
                            topic[4] = "D. " + topic[4];
                        }
                        screen.showCountDown(scene, 5, topic);
                        countdownTimer = setTimeout(() => {
                            console.log("Time out");
                            showTopic = false;
                        }, 15000);
                        answer = message.answer;
                        queationId = message.questionId;
                        sendTopicRequest = false;
                    }
                }
                else if (message.type == "result") {
                    screen.showResult(scene, message.name, message.result);
                    showTopic = false;
                    clearTimeout(countdownTimer);
                    if (message.name == player) {
                        if (message.result) {
                            winNum++;
                        } else {
                            loseNum++;
                        }
                    }   
                    else {
                        if (message.result) {
                            loseNum++;
                        } else {
                            winNum++;
                        }
                    }
                }
                else if (message.type == "cont") {
                    screen.deleteEnd(scene);
                    screen = new Screen(scene);
                    screen.showState(scene, message.role, message.name, false);
                    // 初始化参数
                    preNum = 0;
                    topicNum = 0;
                    showTopic = false;
                    showEnd = false;
                    player.pre = false;
                    player.input.pre = false;
                    player.input.cont = false;
                    clearTimeout(countdownTimer);
                }
                else if (message.type == "logout") {
                    if (playerMap.get(message.socketId) != undefined && playerMap.get(message.socketId) != 0 && playerMap.get(message.socketId) != -1){
                        // 删除 state
                        let position = playerMap.get(message.socketId).model.mesh.position.clone();
                        if (position.z > 4 && position.z < 60) {
                            if (position.x > 4 && position.x < 32) {
                                scene.remove(screen.stateMesh[1 - 1]);
                                screen.isState[1 - 1] = false;
                            }
                            else if (position.x > 32 && position.x < 60) {
                                scene.remove(screen.stateMesh[2 - 1]);
                                screen.isState[2 - 1] = false;
                            }
                        }
                        
                        scene.remove(playerMap.get(message.socketId).model.mesh);
                        playerMap.set(message.socketId, -1);
                    }
                }
            };
    
            // Render loop
            let previousTime = performance.now();
            const animate = () => {
                let currentTime = performance.now();
                let deltaTime = (currentTime - previousTime) / 1000;
                // requestAnimationFrame 是一个浏览器提供的 API，它告诉浏览器你想要执行动画，并且请求浏览器在下一次重绘之前调用指定的回调函数。
                requestAnimationFrame(animate);
    
                if (player.controls.isLocked) {
                    let position1 = player.position.clone();
                    physics.update(deltaTime, player, world);
                    let position2 = player.position.clone();
                    if (position1.x != position2.x || position1.y != position2.y || position1.z != position2.z) {
                        isStill = false;
                        const rotation = player.model.mesh.rotation.clone();
                        if (!isSending) {
                            isSending = true;
                            ws.send(JSON.stringify({
                                type: "position",
                                position: JSON.stringify(position2),
                                rotation: JSON.stringify(rotation),
                                role: role,
                                name: name,
                                userId: userId,
                                modelId: modelId,
                                room: room
                            }));
                            isSending = false;
                        }
                        
                        world.update(player);

                        // 使光源跟随玩家
                        light.position.copy(player.position);
                        light.intensity = 3;
                        light.position.sub(new THREE.Vector3(-50, -50, -50));
                        light.target.position.copy(player.position);

                        if (player.animationId == 0) {
                            player.animationId = 1;
                            if (player.modelWalk) {
                                player.modelWalk.mesh.position.copy(player.model.mesh.position);
                                player.modelWalk.mesh.rotation.copy(player.model.mesh.rotation);
                                scene.remove(player.model.mesh);
                                scene.add(player.modelWalk.mesh);
                                player.model = player.modelWalk;
                            }
                        }
                    } else {
                        if (player.animationId == 1) {
                            player.animationId = 0;
                            if (player.modelStill) {
                                player.modelStill.mesh.position.copy(player.model.mesh.position);
                                player.modelStill.mesh.rotation.copy(player.model.mesh.rotation);
                                scene.remove(player.model.mesh);
                                scene.add(player.modelStill.mesh);
                                player.model = player.modelStill;
                            }
                        }
                        if (!isStill) {
                            isStill = true;
                            ws.send(JSON.stringify({
                                type: "still",
                                room: room
                            }));
                        }
                    }
                    if (player.model && player.model.mesh.visible == true) {
                        player.model.mesh.visible = false;
                    }
                } else {
                    if (player.model && player.model.mesh.visible == false) {
                        player.model.mesh.visible = true;
                    }
                }

                if (player.role != 0) {
                    // 比赛尚未开始
                    if (preNum != 2) {
                        if (player.input.pre == true) {
                            if (!player.pre) {
                                player.pre = true;
                                screen.showState(scene, player.role, name, true);
                                preNum++;

                                ws.send(JSON.stringify({
                                    type: "pre",
                                    role: role,
                                    name: name,
                                    room: room
                                }));
                            }
                        } else if (player.input.pre == false) {
                            if (player.pre) {
                                player.pre = false;
                                screen.showState(scene, player.role, name, false);
                                preNum--;

                                ws.send(JSON.stringify({
                                    type: "depre",
                                    role: role,
                                    name: name,
                                    room: room
                                }));
                            }
                        }
                        if (preNum != 2 || showEnd) {
                            if (player.input.exit == true) {
                                // TODO：退出现在跳转到main页面
                                ws.send(JSON.stringify({
                                    type: "logout",
                                    room: room
                                }));
                                router.push('/main');
                            }
                        }
                    }
                    if (preNum == 2 && !showTopic) {  // 两人都准备好了再开始游戏
                        if (topicNum < 2) {
                            if (!sendTopicRequest) {
                                player.input.answer = null;
                                ws.send(JSON.stringify({
                                    type: "topic",
                                    room: room
                                }));
                                sendTopicRequest = true;
                            }
                        } else {
                            if (!showEnd) {
                                showEnd = true;
                                screen.showEnd(scene);
                                ws.send(JSON.stringify({
                                    type: "end",
                                    user1Id: userId,
                                    user2Id: opponentId,
                                    winNum: winNum,
                                    loseNum: loseNum,
                                    room: room
                                }));
                            }
                            if (player.input.cont == true) {
                                ws.send(JSON.stringify({
                                    type: "cont",
                                    role: role,
                                    name: name,
                                    room: room
                                }));
                            }
                        }
                    } 
                    // 接收到答题结果
                    if (preNum == 2) {
                        if (player.input.answer) {
                            if (player.input.answer < screen.topicMesh.length) {
                                let result = player.input.answer == answer;
                                ws.send(JSON.stringify({
                                    type: "result",
                                    questionId: queationId,
                                    result: result,
                                    userId: userId,
                                    name: name,
                                    room: room
                                }));
                            }
                        }
                    }
                    player.input.answer = null
                    player.input.cont = undefined;
                    player.input.pre = undefined;
                }
    
                renderer.render(
                    scene,
                    player.controls.isLocked ? player.camera : orbitCamera
                );
                stats.update();
                previousTime = currentTime;
                // physics.helpers.clear();
                
                // 更新动画
                if (player.model && player.model.helper) {
                    player.model.helper.update(deltaTime);
                }
                for (let [key, value] of playerMap) {
                    if (value != 0 && value != -1) {
                        if (value.model.helper) {
                            value.model.helper.update(deltaTime);
                        }
                    }
                }
            }
    
            // 窗口大小变化，重新渲染
            window.addEventListener("resize", () => {
                orbitCamera.aspect = window.innerWidth / window.innerHeight;
                orbitCamera.updateProjectionMatrix();
                player.camera.aspect = window.innerWidth / window.innerHeight;
                player.camera.updateProjectionMatrix();
                renderer.setSize(window.innerWidth, window.innerHeight);
            });
    
            // createGUI(scene, world, player);
    
            animate();
        },
    };
    </script>
    
    <style>
    body {
        margin: 0; /* Remove default margin */
    }
    
    #info {
        position: absolute;
        right: 0;
        bottom: 0;
        font-family: sans-serif;
        font-size: 24px;
        color: white;
        margin: 8px;
    }
</style>