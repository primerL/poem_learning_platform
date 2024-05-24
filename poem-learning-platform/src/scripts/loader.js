import * as THREE from "three";
import { MMDLoader } from "three/examples/jsm/loaders/MMDLoader.js";
import { MMDAnimationHelper } from "three/examples/jsm/animation/MMDAnimationHelper.js";

export const modelAnimationMapping = [
    ["../../src/assets/animation/待机动作黑塔.vmd", "../../src/assets/animation/走路(0-39为一组循环).vmd"],
    ["../../src/assets/animation/待机动作钟离.vmd", "../../src/assets/animation/走路(0-39为一组循环).vmd"],
    ["../../src/assets/animation/待机动作转圈.vmd", "../../src/assets/animation/走路(0-39为一组循环).vmd"],
    ["../../src/assets/animation/待机动作云堇.vmd", "../../src/assets/animation/走路(0-39为一组循环).vmd"],
    ["../../src/assets/animation/待机动作公子.vmd", "../../src/assets/animation/走路(0-39为一组循环).vmd"],
    ["../../src/assets/animation/待机动作公子.vmd"],  // 这个动作会报错unknown char code 245.
    ["../../src/assets/animation/待机动作公子.vmd"],
]

export const modelPath = [
    "../../src/assets/model/星穹铁道—流萤无武器/流萤3.0.pmx",
    "../../src/assets/model/星穹铁道-罗刹/罗刹.pmx",
    "../../src/assets/model/星穹铁道—李素裳/李素裳1.0.pmx",
    "../../src/assets/model/星穹铁道—驭空/星穹铁道—驭空（改5）.pmx",
    "../../src/assets/model/星穹铁道—饮月君/星穹铁道—饮月君1708.pmx",
    "../../src/assets/model/星穹铁道—阮·梅/阮·梅1.0.pmx",
    "../../src/assets/model/螺丝咕姆20231219/螺丝咕姆1.0.pmx",
]

export class Loader {
    loadModelWithAnimation(modelPath, vmdPath) {
        return new Promise((resolve, reject) => {
            const loader = new MMDLoader();

            loader.loadWithAnimation(
                modelPath,
                vmdPath,
                (mmd) => {
                    // 调整模型大小，将模型缩小为原来的0.15倍大小
                    mmd.mesh.scale.set(0.15, 0.15, 0.15);
                    mmd.mesh.castShadow = true;
                    mmd.mesh.receiveShadow = true;
                    const helper = new MMDAnimationHelper(mmd.mesh);
                    helper.add(mmd.mesh, {
                        animation: mmd.animation,
                        physics: false 
                    });
                    mmd.helper = helper;

                    // 返回加载的模型
                    resolve(mmd);
                },
                undefined,
                (error) => {
                    reject(error);
                }
            );
        });
    }

    loadModelWithNumber(index1, index2) {
        if (index1 >= 1 && index1 <= modelPath.length && index2 >= 1 && index2 <= modelAnimationMapping[index1 - 1].length) {
            return this.loadModelWithAnimation(modelPath[index1 - 1], modelAnimationMapping[index1 - 1][index2 - 1]);
        } else {
            return Promise.reject(new Error("Invalid model index"));
        }
    }

    // 要先恢复动画的起始位置，再加载新的动画（但是调不出来）
    loadWithAnimation(model, vmdPath) {
        return new Promise((resolve, reject) => {
            const loader = new MMDLoader();

            loader.loadAnimation(
                vmdPath,
                model.mesh,
                (animation) => {
                    if (helper.objects.get(model.mesh)) {
                        const mixer = helper.objects.get(model.mesh).mixer;
                        const AnimationAction = mixer.clipAction(model.animation);
                        console.log(`Current Time: ${mixer.time}`);
                        AnimationAction.time = -0.3;
                        AnimationAction.play();
                        setTimeout(() => {
                            helper.remove(model.mesh);
                            helper.add(model.mesh, {
                                animation: animation,
                                physics: false
                            });
                        }, 100);
                    }
                    
                    console.log("here");
                    resolve(model.mesh);
                },
                undefined,
                (error) => {
                    reject(error);
                }
            );
        });
    }
}

export default new Loader();

