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
    
    export default {
        name: "Scene",
        mounted() {
            this.preNum = 0; // 记录准备状态的人数
            this.topicNum = 0; // 记录已答的题目数
            // TODO：后端传来其他用户的信息（id和位置），需要进行渲染
            // TODO：另一玩家已答题的传输
            // 显示性能监控（FPS）
            const stats = new Stats();
            document.body.appendChild(stats.dom);
    
            // Renderer
            const renderer = new THREE.WebGLRenderer();
            renderer.setPixelRatio(window.devicePixelRatio);
            renderer.setSize(window.innerWidth, window.innerHeight);
            renderer.setClearColor(0x80a0e0);
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
            scene.fog = new THREE.Fog(0x80a0e0, 50, 100);
    
            scene.add(orbitCameraHelper);
    
            const physics = new Physics(scene);
    
            let screen = new Screen(scene);
    
            const player = new Player(scene, screen, this.$route.params);
    
            const light = new THREE.DirectionalLight();
            // 添加光源，包括环境光、平行光
            function setupLight() {
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
    
            const ambientLight = new THREE.AmbientLight();
            ambientLight.intensity = 0.1;
                scene.add(ambientLight);
            }
            setupLight();
    
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
                        // TODO：需要将位置信息发送给后端（和id）
                        world.update(player);

                        // 使光源跟随玩家
                        light.position.copy(player.position);
                        light.position.sub(new THREE.Vector3(-50, -50, -50));
                        light.target.position.copy(player.position);
                    }
                    
                    if (player.role != 0) {
                        // 比赛尚未开始
                        if (this.preNum != 1) {
                            if (player.input.pre == true) {
                                // TODO：将玩家的状态发送给后端
                                if (!player.pre) {
                                    player.pre = true;
                                    screen.showState(scene, player.role, player.name, true);
                                    this.preNum++;
                                }
                            } else if (player.input.pre == false) {
                                // TODO：将玩家的状态发送给后端
                                if (player.pre) {
                                    player.pre = false;
                                    screen.showState(scene, player.role, player.name, false);
                                    this.preNum--;
                                }
                            }
                            if (this.preNum != 1 || this.showEnd) {
                                if (player.input.exit == true) {
                                    // TODO：退出游戏（跳转和向后端发送信息）
                                }
                            }
                        }
                        let countdownTimer;
                        if (this.preNum == 1 && !this.showTopic) {
                            if (this.topicNum < 1) {
                                this.showTopic = true;
                                this.topicNum++;
                                player.input.answer = null;
                                // TODO：请求题目和其答案
                                const topic = [this.topicNum + ". 下列有关文学文化常识的表述，不正确的一项是( )", 
                                "A.“戊申晦”（《登泰山记》）兼用干支纪日法、月相纪日法。“晦”是指农历每月的最后一日，此类纪日法依序还有朔、既望、望。", 
                                "B.“予左迁九江郡司马”（《琵琶行》），古有贵右贱左之说，故称贬官为“左迁”。白居易由京官贬黜至地方，因官职低而着“青衫”。",
                                "C.中国古典诗歌包括古体诗和近体诗。“歌”“行”“吟”是古体诗的文学体裁；近体诗包括律诗和绝句，律诗二、四、六、八句押韵。", 
                                "D.古人的名与字之间往往是有联系的，有的相同或相近，如姚鼐字姬传、白居易字乐天、苏轼字子瞻；有的含义相反，如韩愈字退之。"];
                                screen.showCountDown(scene, 5, topic);
                                countdownTimer = setTimeout(() => {
                                    console.log("Time out");
                                    this.showTopic = false;
                                }, 15000);
                            } else {
                                if (!this.showEnd) {
                                    this.showEnd = true;
                                    screen.showEnd(scene);
                                }
                                if (player.input.cont == true) {
                                    screen.deleteEnd(scene);
                                    screen = new Screen(scene);
                                    screen.showState(scene, player.role, player.name, false);
                                    // 初始化参数
                                    this.preNum = 0;
                                    this.topicNum = 0;
                                    this.showTopic = false;
                                    this.showEnd = false;
                                    player.pre = false;
                                    player.input.pre = false;
                                    player.input.cont = false;
                                    clearTimeout(countdownTimer);
                                }
                            }
                        } 
                        const answer = 1;
                        // 接收到答题结果
                        if (this.preNum == 1) {
                            if (player.input.answer) {
                                if (player.input.answer < screen.topicMesh.length) {
                                    // TODO：将玩家的答题结果发送给后端
                                    screen.showResult(scene, player.name, player.input.answer == answer);
                                    this.showTopic = false;
                                    clearTimeout(countdownTimer);
                                }
                            }
                        }
                        player.input.answer = null
                    }
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