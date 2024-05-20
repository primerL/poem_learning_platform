import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { Quasar } from 'quasar'
import store from './store/index.js'

// Import icon libraries
import '@quasar/extras/material-icons/material-icons.css'

// Import Quasar css
import 'quasar/dist/quasar.css'

const app = createApp(App)

app.use(router).use(store).use(Quasar)
store.subscribe((mutation, state) => {
    console.log('Mutation occurred:', mutation);
    console.log('New state:', state);
});


app.mount('#app')
