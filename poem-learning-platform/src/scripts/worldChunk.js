import * as THREE from 'three';
import { SimplexNoise } from 'three/examples/jsm/math/SimplexNoise.js'
import { RNG } from './rng.js';
import * as RDFLib from 'rdflib';


function loadTexture(path) {
    const textureLoader = new THREE.TextureLoader();
    const texture = textureLoader.load(path);
    texture.colorSpace = THREE.SRGBColorSpace;  //不设置会有颜色偏差
    // 不会模糊显示
    texture.minFilter = THREE.NearestFilter;  //最近点采样
    texture.magFilter = THREE.NearestFilter;  //最近点采样
    return texture;
}

function extractInformationFromRDF(store) {
    const information = {
        classes: {},
        properties: {},
        relationships: []
    };

    // 遍历 RDF 图中的每个三元组
    store.statements.forEach((triple) => {
        const subject = triple.subject.value;
        const predicate = triple.predicate.value;
        const object = triple.object.value;

        // 检查是否是类别定义
        if (predicate === 'http://www.w3.org/1999/02/22-rdf-syntax-ns#type') {
            if (!information.classes[subject]) {
                information.classes[subject] = [];
            }
            information.classes[subject].push(object);
        }
        // 检查是否是属性定义
        else if (predicate.endsWith('#ObjectProperty') || predicate.endsWith('#DatatypeProperty')) {
            if (!information.properties[subject]) {
                information.properties[subject] = [];
            }
            information.properties[subject].push({
                predicate,
                object
            });
        }
        // 检查是否是实体之间的关系
        else {
            information.relationships.push({
                subject,
                predicate,
                object
            });
        }
    });

    return information;
}
let relationships = {};
async function loadOWL(owlFilePath) {
    try {
        // 使用 fetch API 加载文件内容
        const response = await fetch(owlFilePath);
        if (!response.ok) {
            throw new Error('Failed to load OWL file');
        }
        const owlData = await response.text();

        // 解析 RDF 数据
        const store = RDFLib.graph();
        RDFLib.parse(owlData, store, owlFilePath, 'text/turtle');

        const information = extractInformationFromRDF(store);
        // console.log('Properties:', information.properties);
        // console.log('Relationships:', information.relationships);

        const classes = Object.keys(information.classes);
        for (let aclass of classes) {
            const temp = information.relationships.filter((relationship) => {
                return relationship.subject === aclass;
            });
            const parts = aclass.split('#');
            if (temp.length > 0) {
                for (let i = 0; i < temp.length; i++) {
                    const parts = temp[i].predicate.split('#');
                    temp[i].predicate = parts[parts.length - 1];
                }
                relationships[parts[parts.length - 1]] = temp;
            }
        }
    } catch (error) {
        console.error('Error loading OWL file:', error);
    }
}
const owlFilePath = 'http://127.0.0.1:5173/src/assets/owl/block.owl';
loadOWL(owlFilePath);
await loadOWL(owlFilePath);

// 中心点在原点
const geometry = new THREE.BoxGeometry();
const textures = {
    dirt: loadTexture('/textures/dirt.png'),
    grass: loadTexture('/textures/grass.png'),
    grassSide: loadTexture('/textures/grass_side.png'),
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
                // // scale 用于控制噪音的变化速度，越大变化得越平缓（慢）
                // // + this.position.x 使得每个 chunk 的噪音不同
                // const value = simplex.noise(
                //     (x + this.position.x) / this.params.terrain.scale, 
                //     (z + this.position.z) / this.params.terrain.scale
                // );

                // const scaledNoise = this.params.terrain.offset
                //     + this.params.terrain.magnitude * value;

                // let height = Math.floor(this.size.height * scaledNoise);
                // height = Math.max(0, Math.min(height, this.size.height - 1));

                let height = this.size.groundHeight;

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
        let information = {};
        for (let i = 0; i < relationships["grassBlock"].length; i++) {
            if (information[relationships["grassBlock"][i].predicate]) {
                information[relationships["grassBlock"][i].predicate].push(relationships["grassBlock"][i].object);
            }else {                
                information[relationships["grassBlock"][i].predicate] = [relationships["grassBlock"][i].object]; 
            }
        }
        grassMesh.userData = information;
        information = {};
        meshes[1] = grassMesh;
        const airWallMesh = new THREE.InstancedMesh(geometry, airWallMaterial, maxCount);
        airWallMesh.count = 0;
        for (let i = 0; i < relationships["airWallBlock"].length; i++) {
            if (information[relationships["airWallBlock"][i].predicate]) {
                information[relationships["airWallBlock"][i].predicate].push(relationships["airWallBlock"][i].object);
            }else {                
                information[relationships["airWallBlock"][i].predicate] = [relationships["airWallBlock"][i].object]; 
            }
        }
        airWallMesh.userData = information;
        information = {};
        meshes[2] = airWallMesh;
        const screenMesh = new THREE.InstancedMesh(geometry, screenMaterial, maxCount);
        screenMesh.count = 0;
        for (let i = 0; i < relationships["screenWallBlock"].length; i++) {
            if (information[relationships["screenWallBlock"][i].predicate]) {
                information[relationships["screenWallBlock"][i].predicate].push(relationships["screenWallBlock"][i].object);
            }else {                
                information[relationships["screenWallBlock"][i].predicate] = [relationships["screenWallBlock"][i].object]; 
            }
        }
        screenMesh.userData = information;
        information = {};
        meshes[3] = screenMesh;
        const npcMesh = new THREE.InstancedMesh(npcGeometry, npcMaterial, maxCount);
        npcMesh.count = 0;
        for (let i = 0; i < relationships["npcCharacter"].length; i++) {
            if (information[relationships["npcCharacter"][i].predicate]) {
                information[relationships["npcCharacter"][i].predicate].push(relationships["npcCharacter"][i].object);
            }else {                
                information[relationships["npcCharacter"][i].predicate] = [relationships["npcCharacter"][i].object]; 
            }
        }
        npcMesh.userData = information;
        information = {};
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