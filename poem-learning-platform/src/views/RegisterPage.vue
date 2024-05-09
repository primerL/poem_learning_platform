<template>
  <q-card class="my-card" flat bordered>
    <q-card-section horizontal>
      <q-card-section>
        <div id='canva' style="width: 700px; height: 600px;"></div>
      </q-card-section>

      <q-separator vertical />
      <q-card-section>
        <!-- <div class="q-pa-md" style="max-width: 400px">
                      <q-form @submit="onSubmit" @reset="onReset" class="q-gutter-md">
                          <q-input filled v-model="name" label="Your name *" hint="Name and surname" lazy-rules
                              :rules="[val => val && val.length > 0 || 'Please type something']" />

                          <q-input filled type="number" v-model="age" label="Your age *" lazy-rules :rules="[
                          val => val !== null && val !== '' || 'Please type your age',
                          val => val > 0 && val < 100 || 'Please type a real age'
                      ]" />

                          <q-toggle v-model="accept" label="I accept the license and terms" />

                          <div>
                              <q-btn label="Submit" type="submit" color="primary" />
                              <q-btn label="Reset" type="reset" color="primary" flat class="q-ml-sm" />
                          </div>
                      </q-form>

                  </div> -->
      </q-card-section>
    </q-card-section>
  </q-card>

</template>

<script setup>
import { onMounted, ref } from 'vue';
import * as THREE from "three";
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';
import { MMDLoader } from "three/examples/jsm/loaders/MMDLoader.js";
import { MMDAnimationHelper } from "three/examples/jsm/animation/MMDAnimationHelper.js";
import { GUI } from 'three/addons/libs/lil-gui.module.min.js';


onMounted(() => {

  const loader = new MMDLoader();
  const helper = new MMDAnimationHelper();

  const camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 0.1, 2000);
  camera.position.set(0, 0, 20); // 将相机位置移动到模型前方
  camera.lookAt(0, 0, 5); // 让相机指向场景的中心点

  const scene = new THREE.Scene();
  scene.background = new THREE.Color(0x151515);

  const directionalLight = new THREE.DirectionalLight(0xfffffff, 6);
  directionalLight.position.y = 0;
  directionalLight.castShadow = true;
  scene.add(directionalLight);

  function updateLight() {
    directionalLight.target.updateMatrixWorld();
    helper.update();
  }



  const renderer = new THREE.WebGLRenderer({ antialias: true });
  renderer.setPixelRatio(window.devicePixelRatio);
  renderer.outputColorSpace = THREE.LinearSRGBColorSpace


  const container = document.getElementById("canva");
  container.appendChild(renderer.domElement);

  // 获取 canva 盒子的宽度和高度
  const width = container.clientWidth;
  const height = container.clientHeight;

  console.log(width, height);

  // 将宽度和高度传递给渲染器的 setSize 方法
  renderer.setSize(width, height);
  renderer.setClearColor(new THREE.Color(0x000000));

  const pmxfile = '../../src/assets/model/星穹铁道—景元/景元.pmx';

  const gui = new GUI();

  // 天空盒
  const SkyLoader = new THREE.CubeTextureLoader();

  console.log('加载中');

  // loader.load(
  //   pmxfile,
  //   (mesh) => {
  //     mesh.scale.set(0.5, 0.5, 0.5);
  //     mesh.position.set(0, -5, 0);
  //     mesh.material.castShadow = true;
  //     mesh.castShadow = true;
  //     mesh.material.receiveShadow = true;
  //     mesh.receiveShadow = true;
  //     scene.add(mesh);
  //     const modelFolder = gui.addFolder('人物');
  //     const modelParams = { x: 0, y: 0, z: 0 };
  //     modelFolder.add(modelParams, 'x', -200, 200).onChange(() => {
  //       mesh.position.x = modelParams.x;
  //     });
  //     modelFolder.add(modelParams, 'y', -200, 200).onChange(() => {
  //       mesh.position.y = modelParams.y;
  //     });
  //     modelFolder.add(modelParams, 'z', -200, 200).onChange(() => {
  //       mesh.position.z = modelParams.z;
  //     });
  //     console.log('加载成功');
  //   },
  //   (xhr) => {
  //     console.log((xhr.loaded / xhr.total * 100) + '% loaded');
  //   },
  //   (err) => {
  //     console.error(err);
  //   }
  // );

  loader.loadWithAnimation(
    pmxfile,
    '../../src/assets/model/move/neon.vmd',
    (mmd) => {
      const mesh = mmd.mesh;
      mesh.position.y = -11;
      mesh.position.z = -20
      scene.add(mesh);
      helper.add(mmd.mesh, {
        animation: mmd.animation,
      });
      const modelFolder = gui.addFolder('人物');
      const modelParams = { x: 0, y: 0, z: 0 }
      modelFolder.add(modelParams, 'x', -200, 200).onChange(() => {
        mesh.position.x = modelParams.x;
      });
      modelFolder.add(modelParams, 'y', -200, 200).onChange(() => {
        mesh.position.y = modelParams.y;
      });
      modelFolder.add(modelParams, 'z', -200, 200).onChange(() => {
        mesh.position.z = modelParams.z;
      });
    },
    (xhr) => {
      console.log((xhr.loaded / xhr.total * 100) + '% loaded');
    },
    (err) => {
      console.error(err);
    }
  )


  console.log('加载场景')
  // load场景
  loader.load(
    '../../src/assets/model/1/神策府配布版.pmx',
    (mesh) => {
      mesh.position.y = -13;
      // mesh.scale.set(0.05, 0.05, 0.05);
      mesh.material.castShadow = true;
      mesh.castShadow = true;
      mesh.material.receiveShadow = true;
      mesh.receiveShadow = true;
      mesh.material.emissive = new THREE.Color(0xffffff); // 设置物体发光的颜色
      mesh.material.emissiveIntensity = 1; // 设置物体发光的强度

      scene.add(mesh);
      const modelFolder = gui.addFolder('场景');
      const modelParams = { x: 0, y: 0, z: 0 }
      modelFolder.add(modelParams, 'x', -500, 500).onChange(() => {
        mesh.position.x = modelParams.x;
      });
      modelFolder.add(modelParams, 'y', -500, 500).onChange(() => {
        mesh.position.y = modelParams.y;
      });
      modelFolder.add(modelParams, 'z', -500, 500).onChange(() => {
        mesh.position.z = modelParams.z;
      });
      console.log('场景加载成功');
    },
    (xhr) => {
      console.log((xhr.loaded / xhr.total * 100) + '% loaded');
    },
    (err) => {
      console.error(err);
    }
  )

  updateLight();

  // 场景渲染和动画
  function animate() {
    requestAnimationFrame(animate);
    renderer.render(scene, camera);

  }

  animate()

  window.addEventListener('resize', () => {
    const width = window.innerWidth;
    const height = window.innerHeight;
    camera.aspect = width / height;
    camera.updateProjectionMatrix();
    renderer.setSize(width, height);
  });

});

</script>

<style>
.my-card {
  width: 70%;
  height: 70%;
  margin: 5% auto;
  align-items: center;
}
</style>