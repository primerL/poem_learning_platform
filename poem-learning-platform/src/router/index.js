import { createApp } from 'vue';
import { createRouter, createWebHistory } from 'vue-router';
import Scene from '../views/scene.vue';
import App from '../App.vue';

const role = 2;  // 玩家角色，0 为观战者，1 为玩家1，2 为玩家2
const name = "Player";  // 玩家名
const id = 1;
const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            redirect: {
                name: 'Scene',
                params: {
                    role: role,
                    name: name,
                    id: id
                },
            }
        },
        {
            path: '/scene/:role/:name',
            name: 'Scene',
            component: Scene,
            params: {
                role: role,
                name: name,
                id: id
            }
        },
        {
            path: '/test',
            component: () => import('../views/LoginPage.vue')
        }
    ]
});

export default router
