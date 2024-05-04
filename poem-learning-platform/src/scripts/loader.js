import scene from "./scene";
import * as THREE from "three";
import { MMDLoader } from "three/examples/jsm/loaders/MMDLoader.js";
import { MMDAnimationHelper } from "three/examples/jsm/animation/MMDAnimationHelper.js";

// helper用来管理动画
export const helper = new MMDAnimationHelper();

export class Loader {
  loadModels() {
    const loader = new MMDLoader();

    // 单模型
    loader.load(
        '/tuopa/托帕.pmx',
        (mmd) => {
            // 调整模型大小，将模型缩小为原来的一半大小
            mmd.scale.set(0.5, 0.5, 0.5);
            // 添加模型的网格部分到场景中
            scene.add(mmd);
        }
    );

    // 还没试下载的动画能不能成功
    
    // 模型 + 动画
    // loader.loadWithAnimation(
    //   "/public/hutao/胡桃.pmx", // called when the resource is loaded
    //   "./public/move/ayaka-dance.vmd",
    //   function onLoad(mmd) {
    //     const { mesh } = mmd;
    //     helper.add(mmd.mesh, {
    //       animation: mmd.animation,
    //     });

    //     scene.getScene().add(mmd.mesh);
    //   }
    // );

    // 动画 ）
    // loader.loadAnimation(
    //     "./public/move/ayaka-camera.vmd",
    //     function (cameraAnimation) {
    //       helper.add(camera.getCamera(), {
    //         animation: cameraAnimation as THREE.AnimationClip,
    //       });
    //     }
    //   );
  }
}

export default new Loader();
