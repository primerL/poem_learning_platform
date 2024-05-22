<template>
  <q-layout view="hHr lpR fFr" class="bg">
    <div class="q-pa-md">
      <q-card class="my-card" flat bordered>
        <q-card-section horizontal>
          <q-card-section>
            <div id="canva" style="width: 800px; height: 500px"></div>
          </q-card-section>

          <q-separator vertical />

          <q-card-section>
            <div class="text-container">
              <div class="text-h4 font1">注册</div>
            </div>
            <div class="q-pa-md" style="max-width: 400px">
              <q-form @submit="onSubmit" @reset="onReset" class="q-gutter-md">
                <q-input filled v-model="registerName" label="Your name *" hint="Name and surname" lazy-rules :rules="[
                (val) => (val !=null) || 'Please type something',
              ]" />

                <q-input filled type="number" v-model="age" label="Your age *" lazy-rules :rules="[
                (val) =>
                  (val !== null && val !== '') || 'Please type your age',
                (val) => (val > 0 && val < 100) || 'Please type a real age',
              ]" />

                <q-option-group v-model="group" :options="options" color="primary" inline />

                <q-toggle v-model="accept" label="I accept the license and terms" />

                <div>
                  <q-btn label="Submit" type="submit" color="primary" />
                  <q-btn label="Reset" type="reset" color="primary" flat class="q-ml-sm" />
                </div>
              </q-form>
            </div>
          </q-card-section>
        </q-card-section>
      </q-card>
    </div>
  </q-layout>
</template>

<script setup>
import { onMounted, ref, watch } from "vue";
import * as THREE from "three";
import { OrbitControls } from "three/examples/jsm/controls/OrbitControls.js";
import { MMDLoader } from "three/examples/jsm/loaders/MMDLoader.js";
import { MMDAnimationHelper } from "three/examples/jsm/animation/MMDAnimationHelper.js";
import { GUI } from "three/addons/libs/lil-gui.module.min.js";
import axios from "axios";

// 2d
const registerName = ref(null);
const age = ref(0);
const accept = ref(false);

function onSubmit() {
  console.log("Submitted");
}

function onReset() {
  console.log("Reset");
  registerName.value = null;
  age.value = 0;
  accept.value = false;
}


// 3d
let clock, loader, helper, gui, camera, scene;
let directionalLight,
  renderer,
  container,
  pmxfile,
  vmdfile,
  textureLoader,
  controls;
let position = { x: 0, y: 0, z: 0 };
let mesh;

// form
const group = ref("liuying");
const options = [
  {
    label: "流萤",
    value: "liuying",
  },
  {
    label: "罗刹",
    value: "luocha",
  },
  {
    label: "李素裳",
    value: "suchang",
  },
  {
    label: "驭空",
    value: "yukong",
  },
  {
    label: "饮月君",
    value: "yinyue",
  },
];

watch(group, (newValue, oldValue) => {
  console.log("Group changed from", oldValue, "to", newValue);
  switch (newValue) {
    case "liuying":
      pmxfile = "../../src/assets/model/星穹铁道—流萤无武器/流萤3.0.pmx";
      vmdfile = "../../src/assets/animation/待机动作黑塔.vmd";
      position = { x: 6.2, y: -16, z: 1 };
      localStorage.setItem("selectModelId", 1);
      loadMMDwithAnimation(pmxfile, vmdfile, position, 3);
      break;
    case "luocha":
      pmxfile = "../../src/assets/model/星穹铁道-罗刹/罗刹.pmx";
      vmdfile = "../../src/assets/animation/待机动作钟离.vmd";
      position = { x: 6.2, y: -18, z: 1 };
      localStorage.setItem("selectModelId", 2);
      loadMMDwithAnimation(pmxfile, vmdfile, position, 6);
      break;
    case "suchang":
      pmxfile = "../../src/assets/model/星穹铁道—李素裳/李素裳1.0.pmx";
      vmdfile = "../../src/assets/animation/待机动作转圈.vmd";
      position = { x: 6.2, y: -16, z: 1 };
      localStorage.setItem("selectModelId", 3);
      loadMMDwithAnimation(pmxfile, vmdfile, position, 3);
      break;
    case "yukong":
      pmxfile = "../../src/assets/model/星穹铁道—驭空/星穹铁道—驭空（改5）.pmx";
      vmdfile = "../../src/assets/animation/待机动作云堇.vmd";
      position = { x: 6.2, y: -16, z: 1 };
      localStorage.setItem("selectModelId", 4);
      loadMMDwithAnimation(pmxfile, vmdfile, position, 3);
      break;
    case "yinyue":
      pmxfile =
        "../../src/assets/model/星穹铁道—饮月君/星穹铁道—饮月君1708.pmx";
      vmdfile = "../../src/assets/animation/待机动作公子.vmd";
      localStorage.setItem("selectModelId", 5);
      position = { x: 6.2, y: -16, z: 1 };
      loadMMDwithAnimation(pmxfile, vmdfile, position, 3.2);
      break;
    default:
      pmxfile = "../../src/assets/model/星穹铁道—流萤无武器/流萤3.0.pmx";
      vmdfile = "../../src/assets/animation/待机动作黑塔.vmd";
  }
});

function loadMMDwithAnimation(
  pmxfile,
  vmdfile,
  position,
  directionalLight_intensity
) {
  // 先删除之前的模型
  scene.remove(mesh);

  console.log("加载中");

  loader.loadWithAnimation(
    pmxfile,
    vmdfile,
    (mmd) => {
      mesh = mmd.mesh;
      mesh.position.x = position.x;
      mesh.position.y = position.y;
      mesh.position.z = position.z;
      scene.add(mesh);
      helper.add(mmd.mesh, {
        animation: mmd.animation,
        physics: false,
      });
      // const modelFolder = gui.addFolder('人物');
      // const modelParams = { x: 0, y: 0, z: 0 }
      // modelFolder.add(modelParams, 'x', -200, 200).onChange(() => {
      //   mesh.position.x = modelParams.x;
      // });
      // modelFolder.add(modelParams, 'y', -200, 200).onChange(() => {
      //   mesh.position.y = modelParams.y;
      // });
      // modelFolder.add(modelParams, 'z', -200, 200).onChange(() => {
      //   mesh.position.z = modelParams.z;
      // });
    },
    (xhr) => {
      console.log((xhr.loaded / xhr.total) * 100 + "% loaded");
    },
    (err) => {
      console.error(err);
    }
  );

  directionalLight.intensity = directionalLight_intensity;

  directionalLight.target.updateMatrixWorld();
  helper.update();

  window.addEventListener("resize", () => {
    const width = window.innerWidth;
    const height = window.innerHeight;
    camera.aspect = width / height;
    camera.updateProjectionMatrix();
    renderer.setSize(width, height);
  });

  animate();
}

function init() { }

// 场景渲染和动画
function animate() {
  helper.update(clock.getDelta());
  requestAnimationFrame(animate);
  renderer.render(scene, camera);
}

onMounted(() => {
  clock = new THREE.Clock();
  loader = new MMDLoader();
  helper = new MMDAnimationHelper();
  gui = new GUI();
  camera = new THREE.PerspectiveCamera(
    45,
    window.innerWidth / window.innerHeight,
    0.1,
    2000
  );
  scene = new THREE.Scene();
  directionalLight = new THREE.DirectionalLight(0xfffffff);
  renderer = new THREE.WebGLRenderer({ antialias: true });
  container = document.getElementById("canva");
  pmxfile = "../../src/assets/model/星穹铁道—流萤无武器/流萤3.0.pmx";
  textureLoader = new THREE.TextureLoader();
  controls = new OrbitControls(camera, renderer.domElement);

  camera.position.set(0, 0, 20); // 将相机位置移动到模型前方
  camera.lookAt(0, 0, 5); // 让相机指向场景的中心点

  // const modelFolder = gui.addFolder('相机');
  // const modelParams = { x: 0, y: 0, z: 0 }
  // modelFolder.add(modelParams, 'x', -200, 200).onChange(() => {
  //   camera.position.x = modelParams.x;
  // });
  // modelFolder.add(modelParams, 'y', -200, 200).onChange(() => {
  //   camera.position.y = modelParams.y;
  // });
  // modelFolder.add(modelParams, 'z', -200, 200).onChange(() => {
  //   camera.position.z = modelParams.z;
  // });

  scene.background = new THREE.Color(0x151515);

  directionalLight.intensity = 3;
  directionalLight.position.y = 0;
  directionalLight.castShadow = true;
  scene.add(directionalLight);

  function updateLight() {
    directionalLight.target.updateMatrixWorld();
    helper.update();
  }

  renderer.setPixelRatio(window.devicePixelRatio);
  renderer.outputColorSpace = THREE.LinearSRGBColorSpace;

  container.appendChild(renderer.domElement);
  const width = container.clientWidth;
  const height = container.clientHeight;
  console.log(width, height);

  // 将宽度和高度传递给渲染器的 setSize 方法
  renderer.setSize(width, height);
  renderer.setClearColor(new THREE.Color(0x000000));

  // const pmxfile = '../../src/assets/model/星穹铁道—阮·梅/阮·梅1.0.pmx';
  // const pmxfile = '../../src/assets/model/星穹铁道-罗刹/罗刹.pmx'
  // const pmxfile = '../../src/assets/model/星穹铁道—驭空/星穹铁道—驭空（改5）.pmx'
  // const pmxfile = '../../src/assets/model/星穹铁道—饮月君/星穹铁道—饮月君1708.pmx'
  // const pmxfile = '../../src/assets/model/星穹铁道—李素裳/李素裳1.0.pmx'

  console.log("加载中");

  textureLoader.load("../../src/assets/img/bg.png", function (texture) {
    texture.wrapS = texture.wrapT = THREE.RepeatWrapping;
    const floorMaterial = new THREE.MeshBasicMaterial({
      map: texture,
      side: THREE.DoubleSide,
    });
    const floorGeometry = new THREE.PlaneGeometry(800, 200, 5, 5);
    const floor = new THREE.Mesh(floorGeometry, floorMaterial);
    floor.position.y = 0;
    floor.position.z = -200;
    floor.rotation.x = floor.position.y;
    scene.add(floor);
    const modelFolder = gui.addFolder("背景");
    const modelParams = { x: 0, y: 0, z: 0 };
    modelFolder.add(modelParams, "x", -2000, 2000).onChange(() => {
      floor.position.x = modelParams.x;
    });
    modelFolder.add(modelParams, "y", -2000, 2000).onChange(() => {
      floor.position.y = modelParams.y;
    });
    modelFolder.add(modelParams, "z", -2000, 2000).onChange(() => {
      floor.position.z = modelParams.z;
    });
  });

  loader.loadWithAnimation(
    pmxfile,
    "../../src/assets/animation/待机动作黑塔.vmd",
    (mmd) => {
      mesh = mmd.mesh;
      mesh.position.x = 6.2;
      mesh.position.y = -16;
      mesh.position.z = 1;
      scene.add(mesh);
      helper.add(mmd.mesh, {
        animation: mmd.animation,
        physics: false,
      });
      const modelFolder = gui.addFolder("人物");
      const modelParams = { x: 0, y: 0, z: 0 };
      modelFolder.add(modelParams, "x", -200, 200).onChange(() => {
        mesh.position.x = modelParams.x;
      });
      modelFolder.add(modelParams, "y", -200, 200).onChange(() => {
        mesh.position.y = modelParams.y;
      });
      modelFolder.add(modelParams, "z", -200, 200).onChange(() => {
        mesh.position.z = modelParams.z;
      });
    },
    (xhr) => {
      console.log((xhr.loaded / xhr.total) * 100 + "% loaded");
    },
    (err) => {
      console.error(err);
    }
  );

  updateLight();

  window.addEventListener("resize", () => {
    const width = window.innerWidth;
    const height = window.innerHeight;
    camera.aspect = width / height;
    camera.updateProjectionMatrix();
    renderer.setSize(width, height);
  });

  // 场景渲染和动画
  function animate() {
    helper.update(clock.getDelta());
    requestAnimationFrame(animate);
    renderer.render(scene, camera);
  }

  init();
  animate();
});
</script>

<style>
.bg {
  background-image: url(https://s3-us-west-2.amazonaws.com/s.cdpn.io/756881/textured_paper_%402X.png);
  background-blend-mode: multiply;
  background-color: #a9cec2;
  background-repeat: repeat;
  background-size: 800px;
}

.my-card {
  width: 80%;
  height: 70%;
  margin: 5% auto;
  align-items: center;
}

.text-container {
  flex-grow: 1;
  /* 填充剩余空间 */
}

/* 引入自定义字体 */
@font-face {
  font-family: "MyCustomFont";
  /* 自定义字体名称 */
  src: url("../assets/ttf/1.ttf") format("truetype");
  /* 字体文件路径 */
  /* 其他字体属性，如字重、斜体等 */
}

/* 应用自定义字体到元素 */
.text-container {
  font-family: "MyCustomFont", Arial, sans-serif;
  /* 使用自定义字体 */
}
</style>
