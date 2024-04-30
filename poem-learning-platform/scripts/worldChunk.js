import * as THREE from 'three';
import { SimplexNoise } from 'three/examples/jsm/math/SimplexNoise.js'
import { RNG } from './rng.js';


function loadTexture(path) {
    const textureLoader = new THREE.TextureLoader();
    const texture = textureLoader.load(path);
    texture.colorSpace = THREE.SRGBColorSpace;  //不设置会有颜色偏差
    // 不会模糊显示
    texture.minFilter = THREE.NearestFilter;  //最近点采样
    texture.magFilter = THREE.NearestFilter;  //最近点采样
    return texture;
}
// 中心点在原点
const geometry = new THREE.BoxGeometry();
const textures = {
    dirt: loadTexture('textures/dirt.png'),
    grass: loadTexture('textures/grass.png'),
    grassSide: loadTexture('textures/grass_side.png'),
}
const grassMaterial = [
    new THREE.MeshLambertMaterial({map: textures.grassSide}),
    new THREE.MeshLambertMaterial({map: textures.grassSide}),
    new THREE.MeshLambertMaterial({map: textures.grass}),
    new THREE.MeshLambertMaterial({map: textures.dirt}),
    new THREE.MeshLambertMaterial({map: textures.grassSide}),
    new THREE.MeshLambertMaterial({map: textures.grassSide}),
]
const airWallMaterial = new THREE.MeshBasicMaterial({ wireframe: true });
const screenMaterial = new THREE.MeshBasicMaterial({ color: 0x000000 });
const npcGeometry = new THREE.CylinderGeometry(0.5, 0.5, 1.75, 16);
const npcMaterial = new THREE.MeshBasicMaterial({ wireframe: true });
export class WorldChunk extends THREE.Group {
    /**
     * @type {{
     *  id: number
     *  instanceId: number
     * }[][][]}
     */
    data = [];

    constructor(size, params) {
        super();
        this.size = size;
        this.params = params;
        this.loaded = false;
    }

    generate() {
        this.initializeTerrain();
        this.generateTerrain();
        this.generateMeshes();
        this.loaded = true;
    }

    /**
     * 初始化地形
     */
    initializeTerrain() {
        this.data = [];
        for (let x = 0; x < this.size.width; x++) {
            const slice = [];
            for (let y = 0; y < this.size.height; y++) {
                const row = [];
                // 还没有创建实例，id 表示方块的种类，instanceId 表示方块的实例
                for (let z = 0; z < this.size.width; z++) {
                    row.push({
                        id: 0,
                        instanceId: null
                    });
                }
                slice.push(row);
            }
            this.data.push(slice);
        }
    }

    /**
     * 生成地形
     */
    generateTerrain() {
        const rng = new RNG(this.params.seed);
        // 平滑噪音
        const simplex = new SimplexNoise(rng);
        for (let x = 0; x < this.size.width; x++) {
            for (let z = 0; z < this.size.width; z++) {
                // scale 用于控制噪音的变化速度，越大变化得越平缓（慢）
                // + this.position.x 使得每个 chunk 的噪音不同
                const value = simplex.noise(
                    (x + this.position.x) / this.params.terrain.scale, 
                    (z + this.position.z) / this.params.terrain.scale
                );

                const scaledNoise = this.params.terrain.offset
                    + this.params.terrain.magnitude * value;

                let height = Math.floor(this.size.height * scaledNoise);
                height = Math.max(0, Math.min(height, this.size.height - 1));

                // let height = 1;

                for (let y = 0; y <= height; y++) {
                    this.setBlockId(x, y, z, 1);
                }
            }
        }
    }

    /**
     * 生成方块
     */
    generateMeshes() {
        // 清空之前的方块
        this.clear();

        // 缩减同一种块的渲染次数，可以渲染不超过 maxCount 个相同的块
        const maxCount = this.size.width * this.size.height * this.size.width;
        const meshes = {};
        const grassMesh = new THREE.InstancedMesh(geometry, grassMaterial, maxCount);
        grassMesh.castShadow = true;
        grassMesh.receiveShadow = true;
        grassMesh.count = 0;
        meshes[1] = grassMesh;
        const airWallMesh = new THREE.InstancedMesh(geometry, airWallMaterial, maxCount);
        airWallMesh.count = 0;
        meshes[2] = airWallMesh;
        const screenMesh = new THREE.InstancedMesh(geometry, screenMaterial, maxCount);
        screenMesh.count = 0;
        meshes[3] = screenMesh;
        const npcMesh = new THREE.InstancedMesh(npcGeometry, npcMaterial, maxCount);
        npcMesh.count = 0;
        meshes[4] = npcMesh;

        const matrix = new THREE.Matrix4();
        for (let x = 0; x < this.size.width; x++) {
            for (let y = 0; y < this.size.height; y++) {
                for (let z = 0; z < this.size.width; z++) {
                    const blockId = this.getBlock(x, y, z).id;
                    if (blockId !== 0 && blockId !==2 && this.isBlockObscured(x, y, z) === false) {
                        const mesh = meshes[blockId];
                        const instanceId = mesh.count;
                        matrix.setPosition(x, y, z);
                        mesh.setMatrixAt(instanceId, matrix);
                        this.setInstanceId(x, y, z, instanceId);
                        mesh.count++;
                    }
                }
            }
        }

        this.add(...Object.values(meshes));
    }

    getBlock(x, y, z) {
        if (this.inBounds(x, y, z)) {
            return this.data[x][y][z];
        } else {
            return null;
        }
    }

    inBounds(x, y, z) {
        if (x >= 0 && x < this.size.width && y >= 0 && y < this.size.height && z >= 0 && z < this.size.width) {
            return true;
        } else {
            return false;
        }
    }

    setBlockId(x, y, z, id) {
        if (this.inBounds(x, y, z)) {
            this.data[x][y][z].id = id;
        }
    }

    setInstanceId(x, y, z, instanceId) {
        if (this.inBounds(x, y, z)) {
            this.data[x][y][z].instanceId = instanceId;
        }
    }

    isBlockObscured(x, y, z) {
        const up = this.getBlock(x, y + 1, z)?.id ?? 0;
        const down = this.getBlock(x, y - 1, z)?.id ?? 0;
        const left = this.getBlock(x + 1, y, z)?.id ?? 0;
        const right = this.getBlock(x - 1, y, z)?.id ?? 0;
        const forward = this.getBlock(x, y, z + 1)?.id ?? 0;
        const back = this.getBlock(x, y, z - 1)?.id ?? 0;

        if (up === 0 ||
            down === 0 ||
            left === 0 ||
            right === 0 ||
            forward === 0 ||
            back === 0) {
            return false;
        }
        if (up === 2 ||
            down === 2 ||
            left === 2 ||
            right === 2 ||
            forward === 2 ||
            back === 2) {
            return false;
        }
        return true;
    }

    disposeInstances() {
        this.traverse((obj) => {
            if (obj.dispose) {
                obj.dispose();
            }
        });
        this.clear();
    }
}