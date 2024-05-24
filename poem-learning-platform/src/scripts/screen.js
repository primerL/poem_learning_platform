import * as THREE from 'three';
import { Font } from './font.js';
import { TextGeometry } from 'three/examples/jsm/Addons.js';

const textMaterial = new THREE.MeshBasicMaterial({ color: 0x00ff00 });

export class Screen {
    constructor(scene) {
        this.warmtextMesh = null;
        this.stateMesh = [];
        this.topicMesh = [];
        this.resultMesh = null;
        this.isState = [false, false];

        this.font = new Font();
        this.showWarmText(scene);
    }

    async loadFontAsync() {
        try {
            await this.font.loadFont();
        } catch (error) {
            console.error('Failed to load font:', error);
        }
    }

    async showWarmText(scene) {
        this.deleteEnd(scene);
        if (!this.font || !this.font.font) {
            await this.loadFontAsync();
        }

        const warmtextGeometry = new TextGeometry('欢迎进入诗词比赛平台，\n等待双方准备完毕后开始比赛！', {
            font: this.font.font,
            size: 1.5,
            depth: 0.001,
            curveSegments: 12,
            bevelEnabled: false
        });
        const warmtextMesh = new THREE.Mesh(warmtextGeometry, textMaterial);
        warmtextMesh.position.set(50, 22, 53.5);
        warmtextMesh.rotation.y = Math.PI;
        if (!this.warmtextMesh) {
            scene.add(warmtextMesh);
            this.warmtextMesh = warmtextMesh;
        }
    }

    deleteWarmText(scene) {
        scene.remove(this.warmtextMesh);
        this.warmtextMesh = null;
    }

    async showTopic(scene, topic) {
        if (!this.font || !this.font.font) {
            await this.loadFontAsync();
        }
        // 去除所有的'\n'
        topic = topic.map(item => item.replace(/\n/g, ''));
        let topicArray = [];
        for (let i = 0; i < topic[0].length; i += 41) {
            topicArray.push(topic[0].substring(i, i + 41));
        }
        topic[0] = topicArray.join('\n');
        const topicGeometry = new TextGeometry(topic[0], {
            font: this.font.font,
            size: 1,
            depth: 0.001,
            curveSegments: 12,
            bevelEnabled: false
        });
        const topicMesh = new THREE.Mesh(topicGeometry, textMaterial);
        topicMesh.position.set(58, 33, 53.5);
        topicMesh.rotation.y = Math.PI;
        scene.add(topicMesh);
        this.topicMesh = [topicMesh];

        let optionArray = [];
        for (let i = 0; i < topic[1].length; i += 18) {
            optionArray.push(topic[1].substring(i, i + 18));
        }
        topic[1] = optionArray.join('\n');
        const option1Geometry = new TextGeometry(topic[1], {
            font: this.font.font,
            size: 1,
            depth: 0.001,
            curveSegments: 12,
            bevelEnabled: false
        });
        const option1Mesh = new THREE.Mesh(option1Geometry, textMaterial);
        option1Mesh.position.set(58, 29, 53.5);
        option1Mesh.rotation.y = Math.PI;
        scene.add(option1Mesh);
        this.topicMesh.push(option1Mesh);

        optionArray = [];
        for (let i = 0; i < topic[2].length; i += 18) {
            optionArray.push(topic[2].substring(i, i + 18));
        }
        topic[2] = optionArray.join('\n');
        const option2Geometry = new TextGeometry(topic[2], {
            font: this.font.font,
            size: 1,
            depth: 0.001,
            curveSegments: 12,
            bevelEnabled: false
        });
        const option2Mesh = new THREE.Mesh(option2Geometry, textMaterial);
        option2Mesh.position.set(31.5, 29, 53.5);
        option2Mesh.rotation.y = Math.PI;
        scene.add(option2Mesh);
        this.topicMesh.push(option2Mesh);

        optionArray = [];
        for (let i = 0; i < topic[3].length; i += 18) {
            optionArray.push(topic[3].substring(i, i + 18));
        }
        topic[3] = optionArray.join('\n');
        const option3Geometry = new TextGeometry(topic[3], {
            font: this.font.font,
            size: 1,
            depth: 0.001,
            curveSegments: 12,
            bevelEnabled: false
        });
        const option3Mesh = new THREE.Mesh(option3Geometry, textMaterial);
        option3Mesh.position.set(58, 17.5, 53.5);
        option3Mesh.rotation.y = Math.PI;
        scene.add(option3Mesh);
        this.topicMesh.push(option3Mesh);

        if (topic.length > 4) {
            optionArray = [];
            for (let i = 0; i < topic[4].length; i += 18) {
                optionArray.push(topic[4].substring(i, i + 18));
            }
            topic[4] = optionArray.join('\n');
            const option4Geometry = new TextGeometry(topic[4], {
                font: this.font.font,
                size: 1,
                depth: 0.001,
                curveSegments: 12,
                bevelEnabled: false
            });
            const option4Mesh = new THREE.Mesh(option4Geometry, textMaterial);
            option4Mesh.position.set(31.5, 17.5, 53.5);
            option4Mesh.rotation.y = Math.PI;
            scene.add(option4Mesh);
            this.topicMesh.push(option4Mesh);
        }
    }

    deleteTopic(scene) {
        if (this.topicMesh) {
            this.topicMesh.forEach(mesh => {
                scene.remove(mesh);
            });
            this.topicMesh = [];
        }
    }

    // state: false 未准备 true 准备
    async showState(scene, role, name, state) {
        if (role != 1 && role != 2) {
            return;
        }
        this.isState[role - 1] = true;
        if (!this.font || !this.font.font) {
            await this.loadFontAsync();
        }
        if (this.stateMesh[role - 1]) {
            scene.remove(this.stateMesh[role - 1]);
        }
        const stateGeometry = new TextGeometry(name + (state ? ' 已准备' : ' 未准备'), {
            font: this.font.font,
            size: 1,
            depth: 0.001,
            curveSegments: 12,
            bevelEnabled: false
        });

        const stateMesh = new THREE.Mesh(stateGeometry, textMaterial);
        if (role == 1) {
            stateGeometry.computeBoundingBox();
            const boundingBox = stateGeometry.boundingBox;
            // 计算文本的宽度
            const width = boundingBox.max.x - boundingBox.min.x;
            stateMesh.position.set(width + 9.5, 10, 53.5);
        }
        else if (role == 2) {
            stateMesh.position.set(53.5, 10, 53.5);
        }
        stateMesh.rotation.y = Math.PI;
        scene.add(stateMesh);
        this.stateMesh[role - 1] = stateMesh;
    }

    deleteState(scene) {
        if (this.stateMesh) {
            this.stateMesh.forEach(mesh => {
                scene.remove(mesh);
            });
            this.stateMesh = [];
        }
    }

    // 辅助函数，用于实现异步延迟
    sleep(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    // 显示一个倒计时
    async showCountDown(scene, countdownTime, topic) {
        if (this.warmtextMesh) {
            this.deleteWarmText(scene);
            this.deleteState(scene);
        } 
        if (this.resultMesh) {
            await this.sleep(3000);
            this.deleteResult(scene);
        }
        if (this.topicMesh) {
            this.deleteTopic(scene);
        }
        if (!this.font || !this.font.font) {
            await this.loadFontAsync();
        }
    

        // 循环倒计时
        while (countdownTime >= 0) {
            // 显示倒计时
            const countdownGeometry = new TextGeometry(countdownTime.toString(), {
                font: this.font.font,
                size: 5,
                depth: 0.001,
                curveSegments: 12,
                bevelEnabled: false
            });
            const countdownMesh = new THREE.Mesh(countdownGeometry, textMaterial);
            countdownMesh.position.set(32.5, 19, 53.5);
            countdownMesh.rotation.y = Math.PI;
            scene.add(countdownMesh);
            
            // 等待一秒钟
            await this.sleep(1000);

            scene.remove(countdownMesh)

            // 减少倒计时时间
            countdownTime--;
        }

        this.showTopic(scene, topic);
    }

    async showResult(scene, name, result) {
        if (!this.font || !this.font.font) {
            await this.loadFontAsync();
        }
        if (this.topicMesh) {
            this.deleteTopic(scene);
        }

        let results;
        if (result) {
            results = name + " 回答正确！";
        }
        else {
            results = name + " 回答错误！";
        }
        const resultGeometry = new TextGeometry(results, {
            font: this.font.font,
            size: 3,
            depth: 0.001,
            curveSegments: 12,
            bevelEnabled: false
        });
        resultGeometry.computeBoundingBox();
        const boundingBox = resultGeometry.boundingBox;
        // 计算文本的宽度
        const width = boundingBox.max.x - boundingBox.min.x;
        const resultMesh = new THREE.Mesh(resultGeometry, textMaterial);
        resultMesh.position.set(32 + width / 2, 19, 53.5);
        resultMesh.rotation.y = Math.PI;
        scene.add(resultMesh);
        this.resultMesh = resultMesh;
    }

    deleteResult(scene) {
        if (this.resultMesh) {
            scene.remove(this.resultMesh);
            this.resultMesh = null;
        }
    }

    async showEnd(scene) {
        if (this.resultMesh) {
            await this.sleep(3000);
            this.deleteResult(scene);
        }
        if (!this.font || !this.font.font) {
            await this.loadFontAsync();
        }
        if (this.topicMesh) {
            this.deleteTopic(scene);
        }
        const endGeometry = new TextGeometry('比赛结束！', {
            font: this.font.font,
            size: 3,
            depth: 0.001,
            curveSegments: 12,
            bevelEnabled: false
        });
        const endMesh = new THREE.Mesh(endGeometry, textMaterial);
        endMesh.position.set(41, 19, 53.5);
        endMesh.rotation.y = Math.PI;
        if (!this.endMesh) {
            this.endMesh = endMesh;
            scene.add(endMesh);
        }
    }

    deleteEnd(scene) {
        if (this.endMesh) {
            scene.remove(this.endMesh);
            this.endMesh = null;
        }        
    }
}