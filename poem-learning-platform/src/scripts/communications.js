
export class Communications {
  constructor() {
    // WebSocket
    this.socket = null;
    this.id = null;

    // WebSocket 连接状态
    this.isConnected = false;

    // 已连接的对等方数组
    this.peers = {};

    // 本地媒体流（即摄像头和麦克风流）
    this.localMediaStream = null;

    this.initialize();

    this.userDefinedCallbacks = {
      peerJoined: [],
      positions: [],
      peerLeft: [],
      data: [],
    };
  }

  async initialize() {
    // 首先获取用户媒体
    this.localMediaStream = await this.getLocalMedia();

    // 创建本地视频元素
    createPeerDOMElements("local");
    updatePeerDOMElements("local", this.localMediaStream);

    // 初始化 WebSocket 连接
    this.initSocketConnection();
  }

  // 为给定事件添加回调
  on(event, callback) {
    console.log(`设置 ${event} 回调函数.`);
    this.userDefinedCallbacks[event].push(callback);
  }

  sendPosition(position) {
    // if (this.isConnected) {
    //   this.socket.send(JSON.stringify({ type: "move", position }));
    // }
  }

  sendData(data) {
    if (this.isConnected) {
      this.socket.send(JSON.stringify({ type: "data", data }));
    }
  }

  callEventCallback(event, data) {
    console.log(data)
    console.log(event)
    this.userDefinedCallbacks[event].forEach((callback) => {
      callback(data);
    });
  }

  async getLocalMedia() {
    const videoWidth = 180;
    const videoHeight = 160;
    const videoFrameRate = 50;
    let mediaConstraints = {
      audio: true,
      video: {
        width: videoWidth,
        height: videoHeight,
        frameRate: videoFrameRate,
      },
    };

    let stream = null;

    try {
      stream = await navigator.mediaDevices.getUserMedia(mediaConstraints);
    } catch (err) {
      console.log("获取用户媒体失败!");
      console.warn(err);
    }

    return stream;
  }

  // 暂时暂停外发流
  disableOutgoingStream() {
    this.localMediaStream.getTracks().forEach((track) => {
      track.enabled = false;
    });
  }

  // 启用外发流
  enableOutgoingStream() {
    this.localMediaStream.getTracks().forEach((track) => {
      track.enabled = true;
    });
  }

  initSocketConnection() {
    console.log("初始化 WebSocket...");
    this.socket = new WebSocket("ws://localhost:2345/rtc?room=1");

    this.socket.onopen = () => {
      console.log("WebSocket 连接已建立.");
      this.isConnected = true;
    };

    this.socket.onmessage = (event) => {
      const message = JSON.parse(event.data);
      const { type, data, to, from } = message;
      console.log("收到消息:", message);
      console.log("类型:", type);
      console.log("数据:", data);

      if (type === "data") {
        this.callEventCallback("data", data);
      } else if (type === "introduction") {
        const otherPeerIds = message['peers'];
        console.log(data)
        for (let i = 0; i < otherPeerIds.length; i++) {
          if (otherPeerIds[i] != this.id) {
            let theirId = otherPeerIds[i];

            console.log("添加对等方，ID 为 " + theirId);
            this.peers[theirId] = {};

            let pc = this.createPeerConnection(theirId, true);
            this.peers[theirId].peerConnection = pc;

            createPeerDOMElements(theirId);
            this.callEventCallback("peerJoined", theirId);
          }
        }
      } else if (type === "peerConnection") {
        let id = message['id'];
        if (id != this.id && !(id in this.peers)) {
          this.peers[id] = {};
          createPeerDOMElements(id);
          this.callEventCallback("peerConnection peerJoined", id);
        }
      } else if (type === "peerDisconnection") {
        console.log("对等方断开连接");
        let id = message['id'];
        if (id != this.id) {
          this.callEventCallback("peerLeft", id);
          cleanupPeerDomElements(id);
          delete this.peers[id];
        }
      } else if (type === "signal") {
        if (to != this.id) {
          console.log("Socket ID 不匹配");
        }

        console.log("收到信号，来自", from);
        console.log("peers:", this.peers);

        let peer = this.peers[from];
        if (peer.peerConnection) {
          peer.peerConnection.signal(data);
        } else {
          console.log("没有找到正确的 simplepeer 对象");
          let peerConnection = this.createPeerConnection(from, false);

          this.peers[from].peerConnection = peerConnection;

          peerConnection.signal(data);
        }
      } else if (type === "positions") {
        this.callEventCallback("positions", data);
      } else if (type === "yourId") {
        this.id = message['id'];
        console.log("id:", this.id);
      }
    };

    this.socket.onclose = () => {
      console.log("WebSocket 连接已关闭.");
      this.isConnected = false;
      // 在延迟后尝试重连
      setTimeout(() => this.initSocketConnection(), 1000);
    };

    this.socket.onerror = (error) => {
      console.log("WebSocket 错误:", error);
    };
  }

  // 此函数设置对等方连接及相应的 DOM 元素
  createPeerConnection(theirSocketId, isInitiator = false) {
    console.log("连接对等方，ID 为", theirSocketId);
    console.log("是否发起?", isInitiator);

    
    let peerConnection = new SimplePeer({ initiator: isInitiator });
    // simplepeer 生成需要通过 socket 发送的信号
    peerConnection.on("signal", (data) => {
      console.log("发送信号")
      if (this.isConnected) {
        this.socket.send(JSON.stringify({
          type: "signal",
          to: theirSocketId,
          from: this.id,
          data
        }));
      }
    });

    // 当我们有连接时，发送我们的流
    peerConnection.on("connect", () => {
      peerConnection.addStream(this.localMediaStream);
      console.log("发送我们的流");
    });

    // 接收流
    peerConnection.on("stream", (stream) => {
      console.log("接收流");
      updatePeerDOMElements(theirSocketId, stream);
    });

    peerConnection.on("close", () => {
      console.log("接收到关闭事件");
      delete this.peers[theirSocketId];
    });

    peerConnection.on("error", (err) => {
      console.log(err);
    });

    return peerConnection;
  }
}

// 工具 🚂

function createPeerDOMElements(_id) {
  const videoElement = document.createElement("video");
  videoElement.id = _id + "_video";
  videoElement.autoplay = true;
  videoElement.muted = true;

  videoElement.style.width = '120px';
  videoElement.style.height = '90px';

  document.getElementById("videoArea").appendChild(videoElement);

  let audioEl = document.createElement("audio");
  audioEl.setAttribute("id", _id + "_audio");
  audioEl.controls = "controls";
  audioEl.volume = 0; // 初始音量为 0，将由 3D 场景设置

  audioEl.style.display = 'none';

  document.getElementById("videoArea").appendChild(audioEl);

  audioEl.addEventListener("loadeddata", () => {
    audioEl.play();
  });
}

function updatePeerDOMElements(_id, stream) {
  let videoStream = new MediaStream([stream.getVideoTracks()[0]]);
  let audioStream = new MediaStream([stream.getAudioTracks()[0]]);

  if (videoStream) {
    const videoElement = document.getElementById(_id + "_video");
    videoElement.srcObject = videoStream;
  }
  if (audioStream) {
    let audioEl = document.getElementById(_id + "_audio");
    audioEl.srcObject = audioStream;
  }
}

function cleanupPeerDomElements(_id) {
  let videoEl = document.getElementById(_id + "_video");
  if (videoEl != null) {
    videoEl.remove();
  }

  let audioEl = document.getElementById(_id + "_audio");
  if (audioEl != null) {
    audioEl.remove();
  }
}
