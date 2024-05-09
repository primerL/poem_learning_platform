import { createApp } from 'vue';
import { createRouter, createWebHistory } from 'vue-router';
import Scene from '../views/scene.vue';
import Audience from '../views/audience.vue';
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
            // 减少冲突，先另开一个文件（
            path: '/audience/:role/:name',
            name: 'Audience',
            component: Audience,
            params: {
                role: role,
                name: name,
                id: id
            }
        },
        {
            path: '/login',
            component: () => import('../views/LoginPage.vue')
        },
        {
            path: '/open',
            component: ()=> import('../views/OpenView.vue')
        },
        {
            path: '/main',
            component: ()=> import('../views/MainPage.vue')
        },
        {
            path: '/person',
            component: ()=> import('../views/PersonPage.vue')
        },
        {
            path: '/register',
            component: ()=> import('../views/RegisterPage.vue')
        }
    ]
});

export default router
