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

    loadModelWithNumber(index) {
        switch (index) {
            case 1:
                return this.loadModelWithAnimation("../../src/assets/model/星穹铁道—流萤无武器/流萤3.0.pmx", "../../src/assets/animation/疾跑_by_AAAAAAA_fa76438117510229c16270320f38f58a.vmd");
            case 2:
                return this.loadModelWithAnimation("../../src/assets/model/星穹铁道-罗刹/罗刹.pmx", "../../src/assets/animation/疾跑_by_AAAAAAA_fa76438117510229c16270320f38f58a.vmd");
            case 3:
                return this.loadModelWithAnimation("../../src/assets/model/星穹铁道—李素裳/李素裳1.0.pmx", "../../src/assets/animation/疾跑_by_AAAAAAA_fa76438117510229c16270320f38f58a.vmd");
            case 4:
                return this.loadModelWithAnimation("../../src/assets/model/星穹铁道—驭空/星穹铁道—驭空（改5）.pmx", "../../src/assets/animation/疾跑_by_AAAAAAA_fa76438117510229c16270320f38f58a.vmd");
            case 5:
                return this.loadModelWithAnimation("../../src/assets/model/星穹铁道—饮月君/星穹铁道—饮月君1708.pmx", "../../src/assets/animation/疾跑_by_AAAAAAA_fa76438117510229c16270320f38f58a.vmd");
        }
    }
}

export default new Loader();
