import * as THREE from 'three';

const collisionMaterial = new THREE.MeshBasicMaterial({ color: 0xff0000, transparent: true, opacity: 0.2 });
const collisionGeometry = new THREE.BoxGeometry(1.001, 1.001, 1.001);

const contactMaterial = new THREE.MeshBasicMaterial({ color: 0x00ff00, transparent: true, opacity: 0.2 });
const contactGeometry = new THREE.SphereGeometry(0.05, 6, 6);

export class Physics {
    gravity = 32;

    simulationRate = 200;
    timeStep = 1 / this.simulationRate;
    accumulator = 0;

    constructor(scene) {
        this.helpers = new THREE.Group(); // 候选碰撞块和碰撞点的显示
        scene.add(this.helpers);
    }

    /**
     * 玩家移动并解决碰撞
     */
    update(dt, player, world) {
        this.accumulator += dt;
        while (this.accumulator >= this.timeStep) {
            player.velocity.y -= this.gravity * this.timeStep;
            player.applyInputs(this.timeStep);
            player.updateBoundsHelper();
            this.detectCollisions(player, world);
            this.accumulator -= this.timeStep;
        }
    }

    /**
     * 碰撞检测的主函数
     */
    detectCollisions(player, world) {
        player.onGround = false;
        const candidates = this.broadPhase(player, world);
        const collisions = this.narrowPhase(candidates, player);

        if (collisions.length > 0) {
            this.resolveCollisions(collisions, player);
        }
    }

    /**
     * 碰撞检测的第一阶段，广义相交测试
     * @param {Player} player
     * @param {World} world
     * @returns {THREE.Vector3[]}
     */
    broadPhase(player, world) {
        const candidates = [];

        // 计算玩家所在的区域
        const extents = {
            x: {
                min: Math.floor(player.position.x - player.radius),
                max: Math.ceil(player.position.x + player.radius)
            },
            y: {
                min: Math.floor(player.position.y - player.height),
                max: Math.ceil(player.position.y)
            },
            z: {
                min: Math.floor(player.position.z - player.radius),
                max: Math.ceil(player.position.z + player.radius)
            }
        }

        // 遍历区域内的方块
        for (let x = extents.x.min; x <= extents.x.max; x++) {
            for (let y = extents.y.min; y <= extents.y.max; y++) {
                for (let z = extents.z.min; z <= extents.z.max; z++) {
                    const block = world.getBlock(x, y, z);
                    if (block && block.id !== 0) {
                        const blockPos = { x, y, z };
                        candidates.push(blockPos);
                        // this.addCollisionHelper(blockPos);
                    }
                }
            }
        }
        return candidates;
    }

    /**
     * 碰撞检测的第二阶段，细化碰撞测试
     */
    narrowPhase(candidates, player) {
        const collisions = []
        for (const block of candidates) {
            // 1. 找到离玩家中心点最近的块上的点
            const p = player.position;
            const closestPoint = {
                x: Math.max(block.x - 0.5, Math.min(p.x, block.x + 0.5)),
                y: Math.max(block.y - 0.5, Math.min(p.y - (player.height / 2), block.y + 0.5)),
                z: Math.max(block.z - 0.5, Math.min(p.z, block.z + 0.5))
            };

            // 2. 计算最近点是否在玩家的碰撞体内
            const dx = closestPoint.x - p.x;
            const dy = closestPoint.y - (p.y - (player.height / 2));
            const dz = closestPoint.z - p.z;

            if (this.pointInPlayerBoundCylinder(closestPoint, player)) {
                // 3. 计算重叠距离
                const overlapY = player.height / 2 - Math.abs(dy);
                const overlapXZ = player.radius - Math.sqrt(dx * dx + dz * dz);

                // 4. 计算碰撞法线，使得碰撞后玩家能够被推开
                let normal, overlap;
                if (overlapY < overlapXZ) {
                    normal = new THREE.Vector3(0, -Math.sign(dy), 0);
                    overlap = overlapY;
                    player.onGround = true;
                } else {
                    normal = new THREE.Vector3(-dx, 0, -dz).normalize();
                    overlap = overlapXZ;
                }

                collisions.push({
                    block,
                    contactPoint: closestPoint,
                    normal,  // 方向
                    overlap  // 重叠距离
                });

                // this.addContactHelper(closestPoint);
            }
        }
        return collisions;
    }

    /**
     * 碰撞检测的第三阶段，碰撞处理
     * @param {Object[]} collisions
     * @param {Player} player
     */
    resolveCollisions(collisions, player) {
        collisions.sort((a, b) => a.overlap - b.overlap)
        for (const collision of collisions) {
            if (!this.pointInPlayerBoundCylinder(collision.contactPoint, player)) {
                continue;
            }
            // 1. 将玩家推出碰撞体
            let deltaPosition = collision.normal.clone();
            deltaPosition.multiplyScalar(collision.overlap);
            player.position.add(deltaPosition);

            // 2. 取消沿着碰撞法线的速度
            let magnitude = player.worldVelocity.dot(collision.normal);
            let velocityAdjustment = collision.normal.clone().multiplyScalar(magnitude);

            player.applyWorldDeltaVelocity(velocityAdjustment.negate());
        }
    }


    addCollisionHelper(block) {
        const blockMesh = new THREE.Mesh(collisionGeometry, collisionMaterial);
        blockMesh.position.copy(block);
        this.helpers.add(blockMesh);
    }

    addContactHelper(contactPoint) {
        const contactMesh = new THREE.Mesh(contactGeometry, contactMaterial);
        contactMesh.position.copy(contactPoint);
        this.helpers.add(contactMesh);
    }

    /**
     * 判断点是否在玩家的碰撞体内
     * @param {*} p 
     * @param {*} player 
     * @returns 
     */
    pointInPlayerBoundCylinder(p, player) {
        const dx = p.x - player.position.x;
        const dy = p.y - (player.position.y - (player.height / 2));
        const dz = p.z - player.position.z;
        const r_sq = dx * dx + dz * dz;
        return (Math.abs(dy) < player.height / 2) && (r_sq < player.radius * player.radius);
    }
}