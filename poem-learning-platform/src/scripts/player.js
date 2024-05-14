import * as THREE from 'three';
import { PointerLockControls } from 'three/examples/jsm/Addons.js';
import loader from './loader.js';
import { MMDAnimationHelper } from "three/examples/jsm/animation/MMDAnimationHelper.js";

export class Player {
    // 只有在锁定状态下才能移动
    camera = new THREE.PerspectiveCamera(70, window.innerWidth / window.innerHeight, 0.1, 200);
    controls = new PointerLockControls(this.camera, document.body);
    cameraHelper = new THREE.CameraHelper(this.camera);

    maxSpeed = 10;
    input = new THREE.Vector3();
    velocity = new THREE.Vector3();
    #worldVelocity = new THREE.Vector3();

    radius = 0.5;
    height = 1.75;

    jumpSpeed = 10;
    onGround = false;

    constructor(scene, screen, params, name, modelId) {
        if (params.role == 0) {
            this.camera.position.set(32, 32, -2);
        } else if (params.role == 1) {
            this.camera.position.set(17, 32, 25);
            screen.showState(scene, params.role, name, false);
        } else if (params.role == 2) {
            this.camera.position.set(46, 32, 25);
            screen.showState(scene, params.role, name, false);
        }
        // 将用户相关信息存入
        this.role = params.role;
        this.camera.rotation.y += Math.PI;
        scene.add(this.camera);

        loader.loadModelWithNumber(modelId).then(({ mmd, helper }) => {
            mmd.mesh.position.copy(this.camera.position);
            mmd.mesh.rotation.copy(this.camera.rotation);
            mmd.mesh.castShadow = true;
            mmd.mesh.receiveShadow = true;
            scene.add(mmd.mesh);
            this.model = mmd;

            const boundingBox = new THREE.Box3().setFromObject(mmd.mesh);
            this.height = boundingBox.max.y - boundingBox.min.y;
            
            this.modelHelper = helper;
        });

        this.cameraHelper.visible = false;
        scene.add(this.cameraHelper);

        // bind 才能得知是哪个对象调用了事件
        document.addEventListener('keydown', this.onKeyDown.bind(this));
        document.addEventListener('keyup', this.onKeyUp.bind(this));
    }

    updateModel(dt) {
        // 使模型跟随玩家
        let position = this.position.clone();
        position.sub(new THREE.Vector3(0, this.height, 0));
        this.model.mesh.position.copy(position);
        // 获取相机的欧拉角
        const euler = new THREE.Euler();
        euler.setFromQuaternion(this.controls.camera.quaternion, 'YXZ');
        // 只保留 Y 方向上的旋转
        this.model.mesh.rotation.y = euler.y + Math.PI;
        // 更新动画
        this.modelHelper.update(dt);
    }

    get position() {
        return this.camera.position;
    }

    onKeyDown(event) {
        if (!this.controls.isLocked) {
            this.controls.lock();
        }
        switch (event.code) {
            case 'ArrowUp':
                this.input.z = this.maxSpeed;
                break;
            case 'ArrowLeft':
                this.input.x = -this.maxSpeed;
                break;
            case 'ArrowDown':
                this.input.z = -this.maxSpeed;
                break;
            case 'ArrowRight':
                this.input.x = this.maxSpeed;
                break;
            // case 'KeyR':
            //     this.position.set(32, 16, 32);
            //     this.velocity.set(0, 0, 0);
            //     break;
            case 'Digit1':
                this.input.pre = true;
                break;
            case 'Digit2':
                this.input.pre = false;
                break;
            case 'Digit0':
                this.input.exit = true;
                break;
            case 'Digit3':
                this.input.cont = true;
                break;
            case 'KeyA':
                this.input.answer = 1;
                break;
            case 'KeyB':
                this.input.answer = 2;
                break;
            case 'KeyC':
                this.input.answer = 3;
                break;
            case 'KeyD':
                this.input.answer = 4;  
                break;
            case 'Space':
                if (this.onGround) {
                    this.velocity.y += this.jumpSpeed;
                }
                break;
            case 'KeyM' :
                this.input.npcChat = true;
                break;
        }
    }

    onKeyUp(event) {
        switch (event.code) {
            case 'ArrowUp':
                this.input.z = 0;
                break;
            case 'ArrowLeft':
                this.input.x = 0;
                break;
            case 'ArrowDown':
                this.input.z = 0;
                break;
            case 'ArrowRight':
                this.input.x = 0;
                break;
        }
    }

    get worldVelocity() {
        this.#worldVelocity.copy(this.velocity);
        // 实现在相机视角下的世界速度
        this.#worldVelocity.applyEuler(new THREE.Euler(0, this.camera.rotation.y, 0));
        return this.#worldVelocity;
    }

    applyWorldDeltaVelocity(dv) {
        // 将相机视角下的速度转换为世界坐标系下的速度
        dv.applyEuler(new THREE.Euler(0, -this.camera.rotation.y, 0));
        this.velocity.add(dv);
    }

    
    applyInputs(delta) {
        if (this.controls.isLocked) {
            this.velocity.x = this.input.x;
            this.velocity.z = this.input.z;
            this.controls.moveRight(this.velocity.x * delta);
            this.controls.moveForward(this.velocity.z * delta);
            this.position.y += this.velocity.y * delta;  // 由于重力持续下降

            // 显示玩家的位置
            document.getElementById('player-position').innerText = this.toString();
        }
    }

    toString() {
        let str = '';
        str += `X: ${this.position.x.toFixed(3)} `;
        str += `Y: ${this.position.y.toFixed(3)} `;
        str += `Z: ${this.position.z.toFixed(3)}`;
        return str;
    }
}