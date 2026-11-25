# 🍽️ 半闲庭 · 高级餐厅点餐与管理系统

## 📖 项目简介
本项目是一个基于前后端分离架构的高级餐厅 SaaS 管理平台，包含顾客扫码点餐端与老板经营后台。项目不仅实现了从点餐、下单、接单到评价的完整商业闭环，还接入了 **DeepSeek 大语言模型**，打造了 C/B 双端智能 AI Agent（顾客端为点餐管家，老板端为经营参谋）。

## 🛠️ 技术栈
*   **后端**：Spring Boot 3.x, MyBatis, MySQL 8.0, JWT
*   **前端**：Vue 3, Vite, Element Plus, Pinia, ECharts, ECharts
*   **AI 应用**：DeepSeek API (Prompt Engineering 与业务数据注入)
*   **工具**：Git, Maven, Postman

## ✨ 核心亮点
1.  **AI Agent 业务落地**：基于大模型实现意图识别与实时数据注入。顾客端支持自然语言点餐推荐；后台端支持自然语言查询真实营收与热销数据，杜绝模型幻觉。
2.  **防超卖与并发控制**：在订单与订位模块，使用 MySQL 乐观锁与事务控制，确保并发下的数据一致性。
3.  **墨金极简视觉体验**：采用深色模式与香槟金主题色，从登录页到看板，打造极高质感的沉浸式 UI。
4.  **完整的数据看板**：ECharts 绘制营业额折线图、分类占比环形图、热销 Top5 条形图。

## 📸 系统截图 
|---系统登录界面---|
| <img width="3050" height="1644" alt="image" src="https://github.com/user-attachments/assets/57d48576-0e79-4b8b-8b7d-65bbb56d79fe" />
|---顾客首页页头---|
| <img width="3008" height="1642" alt="image" src="https://github.com/user-attachments/assets/ad0e6d59-470b-4a89-8122-c5f3424ea45b" />
|---顾客首页body---|
<img width="1904" height="1638" alt="image" src="https://github.com/user-attachments/assets/4263f469-4d3e-4086-8924-5d93c5f34564" />
|---顾客首页页脚---|
<img width="2966" height="1616" alt="image" src="https://github.com/user-attachments/assets/95d960ce-7b1b-4c9b-a347-7333aef59552" />
|---菜单页面---|
<img width="2966" height="1622" alt="image" src="https://github.com/user-attachments/assets/7aa1036d-b139-4f6a-965e-c66809301e88" />
|---在线订位页面---|
<img width="2986" height="1604" alt="image" src="https://github.com/user-attachments/assets/dd771218-cae0-45ad-b205-2afdd5fde0d5" />
|---查看”我的订单“---|
<img width="2950" height="1640" alt="image" src="https://github.com/user-attachments/assets/898b6bff-c361-4372-a388-4dde6eec70d1" />
|---查看“我的订位记录”|| AI管家对话---|
<img width="2790" height="1590" alt="image" src="https://github.com/user-attachments/assets/d5d8d8d5-eaf4-4c1c-b643-64760b5a2f2d" />
|---后台端经营看板---|
|<img width="3042" height="1646" alt="image" src="https://github.com/user-attachments/assets/ddff68c0-ea7c-4dfa-82d2-45e95f6c5a5e" />
|---点菜订单管理---|
<img width="3040" height="1630" alt="image" src="https://github.com/user-attachments/assets/976f3258-171c-421f-a553-292343b00e8f" />
|---在线桌位管理（可根据日历筛选和生成二维码桌码，提供表格|平面图视图）---|
<img width="3012" height="1654" alt="image" src="https://github.com/user-attachments/assets/d1a9618d-c99d-469b-bf74-6fcea9d74abc" />
|---订位订单管理（附后台Agent对话）---|
<img width="3040" height="1640" alt="image" src="https://github.com/user-attachments/assets/a669cacb-dd71-4c3a-a5aa-c749cce3670f" />
|---用户管理---|
<img width="3026" height="1658" alt="image" src="https://github.com/user-attachments/assets/8b075e62-15c8-4152-b65e-30fafa510e9e" />
|---以下为手机端顾客浏览页面---|
|---用户登录页面---|
<img width="1078" height="2093" alt="194b638d345000f74134fb0f4707eb4c" src="https://github.com/user-attachments/assets/02e8740e-7e5b-4885-aa61-328dd76bec08" />
|---用户注册页面---|
<img width="1053" height="2074" alt="5db9382bbf2d7e1506a3a507b88fd8af" src="https://github.com/user-attachments/assets/2b98a5c3-bb11-4b56-bf36-83c1c5659fd8" />
|---用户扫桌码后进入页面---|
<img width="1073" height="2072" alt="1c27d2b614b62cfe33e73c6317ad2bd7" src="https://github.com/user-attachments/assets/2426fa77-ab96-4875-b5af-dfe69b778f36" />
|---首页1---|
<img width="1074" height="2057" alt="6b7b1d9f9f51b7ccec16a7598f1a3b17" src="https://github.com/user-attachments/assets/6c630d56-e8c2-42bd-8977-3fc52ab336fe" />
|---首页2---|
<img width="1074" height="2060" alt="c06809c19066aecb63763d866b7d6a8d" src="https://github.com/user-attachments/assets/0d860562-626c-432a-8ffa-4081d645dd11" />
|---点餐页面---|
<img width="1057" height="2105" alt="9758f101ab1b202684b00b6d1c9c8da6" src="https://github.com/user-attachments/assets/004fde59-796e-4f58-b650-64378fff8663" />
|---点菜单 | 结算页面---|
<img width="1060" height="2105" alt="7f9c213109d99773e8e144058b545137" src="https://github.com/user-attachments/assets/cd9b3795-5ec1-431a-a10d-c579c81130a7" />
|---预订座位页面---|
<img width="1063" height="2058" alt="d0acdbaede535a5054173b30803c0888" src="https://github.com/user-attachments/assets/502150f4-161e-4034-8104-7720b095f594" />
|---"我的"信息---|
<img width="1080" height="2046" alt="f709e193ed47fb348a0be9a9ba00fa63" src="https://github.com/user-attachments/assets/7849dd37-2e15-47bc-9a06-d643ec3c47f8" />
|---查看我的订单---|
<img width="1054" height="2067" alt="1abd6ea75a9d7d617df4aecbd82bb630" src="https://github.com/user-attachments/assets/b1194558-fae8-40ba-9bca-f4882a946fef" />
|---查看我的预订---|
<img width="1065" height="2093" alt="df0e1fe01c025ae4aced43d9c0fa523b" src="https://github.com/user-attachments/assets/dbb14a4e-8733-4c5d-aa1d-7f1a9ff6ee10" />

## 🚀 本地运行指南
### 1. 数据库准备
导入 `sql/` 目录下的 `.sql` 文件到 MySQL，数据库名为 `templatev3_s`。

### 2. 启动后端
修改 `src/main/resources/application.yaml` 中的数据库密码和 API Key。
使用 IDEA 运行 `PlatformApplication.java` (端口 1000)。

### 3. 启动前端
```bash
cd web
npm install
npm run dev
