import axios from "axios";
import router from "../router";
import { ElMessage } from "element-plus";

// 设置 Axios 的默认基础 URL
axios.defaults.baseURL = import.meta.env.VITE_APP_API_URL;

// 创建 Axios 实例
const http = axios.create({
    timeout: 5000, headers: {
        'Content-Type': 'application/json'
    }
});

// 请求拦截器
http.interceptors.request.use(config => {
    if (localStorage.getItem("token")) {
        config.headers["token"] = localStorage.getItem("token");
    }
    return config;
});

// 响应拦截器
http.interceptors.response.use(response => {
    return response.data;
}, error => {
    //  核心修复 1：必须先判断 error.response 是否存在（网络断开/跨域拦截）
    if (!error.response) {
        ElMessage({ message: "网络异常，请检查后端是否启动", type: "error" });
        return Promise.reject(error);
    }

    // 👇 核心修复 2：如果存在，再根据状态码处理
    switch (error.response.status) {
        case 401:
            localStorage.removeItem("token");
            ElMessage({ message: "请先登录", type: "error" });
            router.push("/login");
            break;
        case 409:
            ElMessage({ message: error.response.data.data, type: "error" });
            break;
        case 404:
            ElMessage({ message: "接口未找到", type: "error" });
            break;
        case 500:
            ElMessage({ message: "服务异常", type: "error" });
            break;
        default:
            break;
    }

    //  核心修复 3：无论如何都要把错误抛出去，防止组件里的 .catch() 失效
    return Promise.reject(error);
});

export default http;