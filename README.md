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
