<template>
    <div>
        <div id="app"></div>
        <div id="info">
            <div id="player-position"></div>
        </div>
        <button id="connectBtn">加入音视频通讯</button>
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

    // FIXME: 增加导入
    import { startWebRTC } from '../scripts/webrtc.js'

    // TODO：未考虑多种模型的个性化选择
    export default {
        name: "Audience",
        mounted() {
            const role = this.$route.params.role;

            let preNum = 0; // 记录准备状态的人数
            let topicNum = 0; // 记录已答的题目数
            let showTopic = false; // 是否显示题目
            let countdownTimer;
            let answer = 0;
            let queationId = 0;
            let showEnd = false; // 是否显示结束界面

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
    
            scene.add(orbitCameraHelper);
    
            const physics = new Physics(scene);

            let screen = new Screen(scene);
    
            const player = new Player(scene, screen, this.$route.params);
    
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
            const ws = new WebSocket("ws://localhost:2345/ws");
            let isSending = false; // 用于标记是否正在发送消息
            ws.onopen = function(event) {
                // TODO：name 应该从localStorage中得到
                const loginObject = {
                    type: "login",
                    role: role, 
                    name: "player" 
                }
                ws.send(JSON.stringify(loginObject));                
            };
            ws.onmessage = function(event) {
                console.log("Received message: ", event.data);
                const message = JSON.parse(event.data);
                if (message.type == "login") {
                    // 导入模型
                    loader.loadModelWithAnimation('../../src/assets/model/星穹铁道—阮·梅/阮·梅1.0.pmx', '../../src/assets/animation/疾跑_by_AAAAAAA_fa76438117510229c16270320f38f58a.vmd').then(({ mmd, helper }) => {
                        if (message.role == 0) {
                            mmd.mesh.position.set(32, 16, 25);
                        } else if (message.role == 1) {
                            mmd.mesh.position.set(17, 16, 25);
                        } else if (message.role == 2) {
                            mmd.mesh.position.set(46, 16, 25);
                        }
                        mmd.mesh.rotation.set(0, Math.PI, 0);
                        mmd.mesh.castShadow = true;
                        mmd.mesh.receiveShadow = true;
                        scene.add(mmd.mesh);
                        const boundingBox = new THREE.Box3().setFromObject(mmd.mesh);
                        const height = boundingBox.max.y - boundingBox.min.y;
                        playerMap.set(message.socketId, {mesh: mmd.mesh, height: height});
                    });
                    // 显示状态
                    if (!screen.isState[message.role - 1]) {
                        screen.showState(scene, message.role, message.name, false);
                    }
                }
                else if (message.type == "position") {
                    const position = JSON.parse(message.position);
                    const rotation = JSON.parse(message.rotation);
                    
                    if (playerMap.get(message.socketId) == undefined) {
                        playerMap.set(message.socketId, 0);
                        loader.loadModelWithAnimation('../../src/assets/model/星穹铁道—阮·梅/阮·梅1.0.pmx', '../../src/assets/animation/疾跑_by_AAAAAAA_fa76438117510229c16270320f38f58a.vmd').then(({ mmd, helper }) => {
                            if (playerMap.get(message.socketId) == 0) {
                                const boundingBox = new THREE.Box3().setFromObject(mmd.mesh);
                                const height = boundingBox.max.y - boundingBox.min.y;

                                mmd.mesh.position.set(position.x, position.y - height, position.z);
                                mmd.mesh.rotation.set(rotation.x, rotation.y, rotation.z);

                                mmd.mesh.castShadow = true;
                                mmd.mesh.receiveShadow = true;
                                
                                scene.add(mmd.mesh);
                                playerMap.set(message.socketId, {mesh: mmd.mesh, height: height});
                            }
                        });
                        
                        if (!screen.isState[message.role - 1]) {
                            screen.showState(scene, message.role, message.name, false);
                        }
                    } else {
                        if (playerMap.get(message.socketId) != 0 && playerMap.get(message.socketId) != -1){
                            playerMap.get(message.socketId).mesh.position.set(position.x, position.y - playerMap.get(message.socketId).height, position.z);
                            playerMap.get(message.socketId).mesh.rotation.set(rotation._x, rotation._y, rotation._z);
                        }
                    }
                }
                else if (message.type == "pre") {
                    console.log("pre")
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
                    }
                }
                else if (message.type == "result") {
                    screen.showResult(scene, message.name, message.result);
                    showTopic = false;
                    clearTimeout(countdownTimer);
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
                    if (playerMap.get(message.socketId) != undefined && playerMap.get(message.socketId) != 0){
                        // 删除 state
                        let position = playerMap.get(message.socketId).mesh.position.clone();
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
                        
                        scene.remove(playerMap.get(message.socketId).mesh);
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

                        const rotation = player.model.mesh.rotation.clone();
                        if (!isSending) {
                            isSending = true;
                            // TODO：name 应该从localStorage中得到
                            ws.send(JSON.stringify({
                                type: "position",
                                position: JSON.stringify(position2),
                                rotation: JSON.stringify(rotation),
                                role: role,
                                name: "player"
                            }));
                            isSending = false;
                        }
                        
                        world.update(player);

                        // 使光源跟随玩家
                        light.position.copy(player.position);
                        light.intensity = 3;
                        light.position.sub(new THREE.Vector3(-50, -50, -50));
                        light.target.position.copy(player.position);
                    }
                }

                if (player.role != 0) {
                    // 比赛尚未开始
                    if (preNum != 1) {
                        if (player.input.pre == true) {
                            if (!player.pre) {
                                player.pre = true;
                                screen.showState(scene, player.role, player.name, true);
                                preNum++;

                                // TODO：玩家名可能要从localStorage中得到
                                ws.send(JSON.stringify({
                                    type: "pre",
                                    role: role,
                                    name: "player"
                                }));
                            }
                        } else if (player.input.pre == false) {
                            if (player.pre) {
                                player.pre = false;
                                screen.showState(scene, player.role, player.name, false);
                                preNum--;

                                // TODO：玩家名可能要从localStorage中得到
                                ws.send(JSON.stringify({
                                    type: "depre",
                                    role: role,
                                    name: "player"
                                }));
                            }
                        }
                        if (preNum != 2 || showEnd) {
                            if (player.input.exit == true) {
                                // TODO：退出游戏（跳转和向后端发送信息）
                            }
                        }
                    }
                    if (preNum == 2 && !showTopic) {  // 两人都准备好了再开始游戏
                        if (topicNum < 2) {
                            player.input.answer = null;
                            ws.send(JSON.stringify({
                                type: "topic",
                            }));
                        } else {
                            if (!showEnd) {
                                showEnd = true;
                                screen.showEnd(scene);
                            }
                            if (player.input.cont == true) {
                                // TODO：name 应该从localStorage中得到
                                ws.send(JSON.stringify({
                                    type: "cont",
                                    role: role,
                                    name: "player"
                                }));
                            }
                        }
                    } 
                    // 接收到答题结果
                    if (preNum == 2) {
                        if (player.input.answer) {
                            if (player.input.answer < screen.topicMesh.length) {
                                let result = player.input.answer == answer;
                                // TODO：userId 和 name 应该从localStorage中得到
                                ws.send(JSON.stringify({
                                    type: "result",
                                    questionId: queationId,
                                    result: result,
                                    userId: 1,
                                    name: "player"
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

            // FIXME:
            // 连接到rtc
            document.getElementById("connectBtn").addEventListener("click", function() {
                // console.log("here")
                // TODO: 修改样式
                startWebRTC();
            });
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
    
    #connectBtn {
        position: absolute;
        right: 0;
    }
</style>