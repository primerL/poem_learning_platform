import { FontLoader } from 'three/addons/loaders/FontLoader.js'

const fonturl='/fonts/ali.json'
export class Font {
    constructor(){
        this.font=null;  // 字体
        this.loaded=false;  // 是否加载完成
    }

    loadFont(){
        let self=this;
        return new Promise((resolve, reject) => {
            const loader = new FontLoader();
            loader.load(fonturl,
                function onLoad(fontok) {
                    self.font = fontok;
                    self.loaded = true;
                    console.log('Font loaded');
                    resolve();
                },
                function onProgress() {

                },
                function onError(e) {
                    console.log("FontLoadFunction" + e);
                    reject(e);
                }
            );
        });
    }
}