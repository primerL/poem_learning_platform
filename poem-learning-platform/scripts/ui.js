// lil-gui
import {GUI} from 'three/addons/libs/lil-gui.module.min.js'

export function createGUI(scene, world, player) {
    const gui = new GUI();

    const sceneFolder = gui.addFolder('Scene');
    sceneFolder.add(scene.fog, 'near', 0, 100).name('Fog Near');
    sceneFolder.add(scene.fog, 'far', 0, 100).name('Fog Far');

    // gui 需要一个对象和一个属性名作为参数，而不是单独的属性值
    const terrainFolder = gui.addFolder('Terrain');
    terrainFolder.add(world, 'drawDistance', 0, 10, 1).name('Draw Distance');
    terrainFolder.add(world.params, 'seed', 0, 100000).name('Seed');
    terrainFolder.add(world.params.terrain, 'scale', 10, 100).name('Scale');
    terrainFolder.add(world.params.terrain, 'magnitude', 0, 1).name('Magnitude');
    terrainFolder.add(world.params.terrain, 'offset', 0, 1).name('Offset');

    const playerFolder = gui.addFolder('Player');
    playerFolder.add(player, 'maxSpeed', 1, 20).name('Max Speed');
    playerFolder.add(player.cameraHelper, 'visible').name('Camera Helper');

    // 使得调整属性值自动更改
    gui.onChange(()=>{
        world.generate();
    });
}