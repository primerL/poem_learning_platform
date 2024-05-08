import * as THREE from 'three';
import { WorldChunk } from './worldChunk.js';

export class World extends THREE.Group {
    params = {
        seed: 0,
        terrain: {
            scale: 30,
            magnitude: 0,
            offset: 0.2
        }
    };

    chunkSize = {
        width: 64,
        height: 32,
        groundHeight: 1
    };

    // 用户可以看到的区块数量
    drawDistance = 1;

    // 是否异步加载区块
    asyncLoading = true;

    constructor(seed = 0) {
        super();
        this.seed = seed;
    }

    generate() {
        this.disposeChunks();

        for (let x = -this.drawDistance; x <= this.drawDistance; x++) {
            for (let z = -this.drawDistance; z <= this.drawDistance; z++) {
                const chunk = new WorldChunk(this.chunkSize, this.params);
                chunk.position.set(x * this.chunkSize.width, 0, z * this.chunkSize.width);
                chunk.generate();
                chunk.userData = { x, z }; // 保存坐标
                this.add(chunk);
            }
        }

        this.generateAirWalls();
        this.generateScreen();
        this.generateArena();
        this.generateNPC();
        this.generateTable();
    }

    /**
     * 返回世界坐标（player）对应的区块坐标
     * @param {*} x 
     * @param {*} y 
     * @param {*} z 
     */
    worlToChunkCoords(x, y, z) {
        const chunkCoords = {
            x: Math.floor(x / this.chunkSize.width),
            z: Math.floor(z / this.chunkSize.width)
        };
        const blockCoords = {
            x: x - this.chunkSize.width * chunkCoords.x,
            y: y,
            z: z - this.chunkSize.width * chunkCoords.z
        };
        return {
            chunk: chunkCoords,
            block: blockCoords
        };
    }

    /**
     * 获得指定坐标的区块
     * @param {*} chunkX 
     * @param {*} chunkZ 
     * @returns 
     */
    getChunk(chunkX, chunkZ) {
        return this.children.find((chunk) => {
            const { x, z } = chunk.userData;
            return x === chunkX && z === chunkZ;
        });
    }

    /**
     * 返回世界坐标（player）对应的方块
     * @param {*} x 
     * @param {*} y 
     * @param {*} z 
     * @returns 
     */
    getBlock(x, y, z) {
        const coords = this.worlToChunkCoords(x, y, z);
        const chunk = this.getChunk(coords.chunk.x, coords.chunk.z);
        if (chunk && chunk.loaded) {
            return chunk.getBlock(coords.block.x, coords.block.y, coords.block.z);
        } else {
            return null;
        }
    }

    disposeChunks() {
        this.traverse((chunk) => {
            if (chunk.disposeInstances) {
                chunk.disposeInstances();
            }
        });
        this.clear();
    }

    getVisibleChunks(player) {
        const visibleChunks = [];

        const coords = this.worlToChunkCoords(player.position.x, player.position.y, player.position.z);
        const chunkX = coords.chunk.x;
        const chunkZ = coords.chunk.z;

        for (let x = chunkX - this.drawDistance; x <= chunkX + this.drawDistance; x++) {
            for (let z = chunkZ - this.drawDistance; z <= chunkZ + this.drawDistance; z++) {
                visibleChunks.push({ x, z });
            }
        }

        return visibleChunks;
    }

    getChunksToAdd(visibleChunks) {
        return visibleChunks.filter((chunk) => {
            const chunkExists = this.children
                .map((obj) => obj.userData)
                .find(({ x, z }) => (
                    chunk.x === x && chunk.z === z
                ));
            return !chunkExists;
        });
    }

    removeUnuesdChunks(visibleChunks) {
        const chunksToRemove = this.children.filter((chunk) => {
            const { x, z } = chunk.userData;
            const chunkExists = visibleChunks
                .find((visibleChunk) => (
                    visibleChunk.x === x && visibleChunk.z === z
                ));
            return !chunkExists;
        });

        for (const chunk of chunksToRemove) {
            chunk.disposeInstances();
            this.remove(chunk);
        }
    }

    generateChunk(x, z) {
        const chunk = new WorldChunk(this.chunkSize, this.params, this.dataStore);
        chunk.position.set(x * this.chunkSize.width, 0, z * this.chunkSize.width);

        if (this.asyncLoading) {
            // 异步加载
            requestIdleCallback(chunk.generate.bind(chunk), { timeout: 1000 });
        } else {
            chunk.generate();
        }
        chunk.userData = { x, z };
        this.add(chunk);
    }

    update(player) {
        // 1. 找到玩家可见的区块
        const visibleChunks = this.getVisibleChunks(player);
        // 保证 (0, 0) 区块始终存在
        if (!visibleChunks.find((chunk) => chunk.x === 0 && chunk.z === 0)) {
            visibleChunks.push({ x: 0, z: 0 });
        }
        // 2. 与当前所有区块进行比较，找到需要添加和移除的区块
        const chunksToAdd = this.getChunksToAdd(visibleChunks);
        // 3. 移除不需要的区块
        this.removeUnuesdChunks(visibleChunks);
        // 4. 添加新的区块
        for (const chunk of chunksToAdd) {
            this.generateChunk(chunk.x, chunk.z);
        }
    }

    /**
     * 在 (0, 0) 区块四周添加空气墙
     */
    generateAirWalls() {
        const chunk = this.getChunk(0, 0);
        let height = 0;
        for (let y = 0; y < this.chunkSize.height; y++) {
            if (chunk.getBlock(0, y, 0).id === 0) {
                height = y;
                break;
            }
        }
        for (let x = 4; x < this.chunkSize.width - 4; x++) {
            for (let y = height; y < height + 5; y++) {
                let z = 4;
                chunk.setBlockId(x, y, z, 2);
            }
        }
        for (let x = 4; x < this.chunkSize.width - 4; x++) {
            for (let y = height; y < height + 5; y++) {
                let z = this.chunkSize.width - 5;
                chunk.setBlockId(x, y, z, 2);
            }
        }
        for (let z = 4; z < this.chunkSize.width - 4; z++) {
            for (let y = height; y < height + 5; y++) {
                let x = 4;
                chunk.setBlockId(x, y, z, 2);
            }
        }
        for (let z = 4; z < this.chunkSize.width - 4; z++) {
            for (let y = height; y < height + 5; y++) {
                let x = this.chunkSize.width - 5;
                chunk.setBlockId(x, y, z, 2);
            }
        }

        // 在中间添加空气墙
        for (let z = 4; z < this.chunkSize.width - 4; z++) {
            for (let y = height; y < height + 5; y++) {
                let x = this.chunkSize.width / 2;
                chunk.setBlockId(x, y, z, 2);
            }
        }
    }

    /**
     * 生成大屏
     */
    generateScreen() {
        const chunk = this.getChunk(0, 0);
        let height = 0;
        for (let y = 0; y < this.chunkSize.height; y++) {
            if (chunk.getBlock(0, y, 0).id === 0) {
                height = y;
                break;
            }
        }
        for (let x = 8; x < this.chunkSize.width - 8; x++) {
            for (let y = height + 7; y < height + 30; y++) {
                let z = this.chunkSize.width - 10;
                chunk.setBlockId(x, y, z, 3);
            }
        }
    }

    /**
     * 生成擂台
     */
    generateArena() {
        const chunk = this.getChunk(0, 0);
        let height = 0;
        for (let y = 0; y < this.chunkSize.height; y++) {
            if (chunk.getBlock(0, y, 0).id === 0) {
                height = y;
                break;
            }
        }

        for (let x = 15; x < 20; x++) {
            for (let z = x + 10; z < x + 12; z++) {
                for (let y = height; y < height + 2; y++) {
                    chunk.setBlockId(x, y, z, 3);
                }
            }
        }

        const width = this.chunkSize.width;
        for (let x = width - 20; x < width - 15; x++) {
            for (let z = 9 + width - x; z < 11 + width - x; z++) {
                for (let y = height; y < height + 2; y++) {
                    chunk.setBlockId(x, y, z, 3);
                }
            }
        }
    }


    /**
     * 生成npc
     */
    generateNPC() {
        const chunk = this.getChunk(0, 0);
        let height = 0;
        for (let y = 0; y < this.chunkSize.height; y++) {
            if (chunk.getBlock(0, y, 0).id === 0) {
                height = y;
                break;
            }
        }
        chunk.setBlockId(20, height + 1, 1, 4);
        chunk.setBlockId(this.chunkSize.width - 20, height + 1, 1, 4);
    }

    /**
     * 生成献花的桌子
     */
    generateTable() {
        const chunk = this.getChunk(0, 0);
        let height = 0;
        for (let y = 0; y < this.chunkSize.height; y++) {
            if (chunk.getBlock(0, y, 0).id === 0) {
                height = y;
                break;
            }
        }
        const width = this.chunkSize.width;
        chunk.setBlockId(30, height, 3, 3);
        chunk.setBlockId(width - 30, height, 3, 3);
        chunk.setBlockId(30, height, 0, 3); 
        chunk.setBlockId(width - 30, height, 0, 3);

        for (let x = 30; x <= width - 30; x++) {
            for (let z = 0; z <= 3; z++) {
                chunk.setBlockId(x, height + 1, z, 3);
            }
        }

        chunk.disposeInstances();
        chunk.generateMeshes();
    }
}