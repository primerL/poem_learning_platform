import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import { World } from './world.js';
import Stats from 'three/examples/jsm/libs/stats.module.js';
import { createGUI } from './ui.js';
import { Player } from './player.js';
import { Physics } from './physics.js';

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
const orbitCamera = new THREE.PerspectiveCamera(60, window.innerWidth / window.innerHeight);
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

const player = new Player(scene);

const physics = new Physics(scene);

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
function animate() {
    let currentTime = performance.now();
    let deltaTime = ( currentTime - previousTime ) / 1000;
    // requestAnimationFrame 是一个浏览器提供的 API，它告诉浏览器你想要执行动画，并且请求浏览器在下一次重绘之前调用指定的回调函数。
    requestAnimationFrame(animate);

    if (player.controls.isLocked) {
        physics.update(deltaTime, player, world);
        world.update(player);
        
        // 使光源跟随玩家
        light.position.copy(player.position);
        light.position.sub(new THREE.Vector3(-50, -50, -50));
        light.target.position.copy(player.position);
    }
    
    renderer.render(scene, player.controls.isLocked ? player.camera : orbitCamera);
    stats.update();
    previousTime = currentTime;
    // physics.helpers.clear();
}

// 窗口大小变化，重新渲染
window.addEventListener('resize', () => {
    orbitCamera.aspect = window.innerWidth / window.innerHeight;
    orbitCamera.updateProjectionMatrix();
    player.camera.aspect = window.innerWidth / window.innerHeight;
    player.camera.updateProjectionMatrix();
    renderer.setSize(window.innerWidth, window.innerHeight);
});

createGUI(scene, world, player);

animate();