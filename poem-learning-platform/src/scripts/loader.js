import * as THREE from "three";
import { MMDLoader } from "three/examples/jsm/loaders/MMDLoader.js";
import { MMDAnimationHelper } from "three/examples/jsm/animation/MMDAnimationHelper.js";

// helper用来管理动画
export const helper = new MMDAnimationHelper();

export class Loader {
    loadModel(modelPath) {
        return new Promise((resolve, reject) => {
            const loader = new MMDLoader();
        
            loader.load(
                modelPath,
                (mmd) => {
                    // 调整模型大小，将模型缩小为原来的0.15倍大小
                    mmd.scale.set(0.15, 0.15, 0.15);
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

    loadModelWithAnimation(modelPath, vmdPath) {
        return new Promise((resolve, reject) => {
            const loader = new MMDLoader();

            loader.loadWithAnimation(
                modelPath,
                vmdPath,
                (mmd) => {
                    // 调整模型大小，将模型缩小为原来的0.15倍大小
                    mmd.mesh.scale.set(0.15, 0.15, 0.15);
                    helper.add(mmd.mesh, {
                        animation: mmd.animation,
                        physics: false // 是否添加物理效果
                    });
                    // 返回加载的模型
                    resolve({mmd, helper});
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
