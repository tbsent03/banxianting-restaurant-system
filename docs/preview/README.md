# 界面设计稿（预览用）

这 7 张 PNG 是**设计稿**，不是运行中的系统截图。用途：开工前先确认"高级感"到底长什么样，避免写完再返工。

- 生成方式：手写 HTML/CSS（`src\` 目录）→ 本机 Chrome 无头模式渲染 → PNG（1440 宽，手机稿 390 宽）
- 色板 / 字号 / 间距 / 圆角全部取自《餐厅点餐系统-施工手册.md》§6（墨金暗调方案 A）
- 照片位置是**占位块**（右下角 `PHOTO 4:3`），正式版换成统一的真实菜品摄影；本机无网络，未引入任何外部图片与 CDN 字体

## 文件清单

| 文件 | 内容 | 对应里程碑 |
|---|---|---|
| `01-前台首页.png` | Hero + 招牌三卡 + 分类条 + 主厨手记 + 订位 CTA + 页脚 | M2 |
| `02-菜单.png` | 左侧分类竖排 + 三列菜品网格 + 排序搜索 | M2 |
| `03-菜品详情.png` | 左大图 + 右信息（规格/辣度/配酒/过敏原）+ 底部固定加购栏 | M2 |
| `04-桌码点餐-手机.png` | 390×844，扫码进店带桌号、分类 chips、悬浮购物车条 | M5 |
| `05-后台看板.png` | 4 指标卡 + 近 7 日折线 + 分类占比环 + 热销 Top5 + 待处理 | M7 |
| `06-后台菜品管理.png` | 搜索卡 / 操作区 / 表格 + 分页（三段式） | M1 |
| `07-后台订单与详情抽屉.png` | 列表只 7 列，完整信息进右侧 480px 抽屉 | M4 |

`index.html` 是总览页（一次滚动看完 7 张）。

## 源文件（`src\`）

| 文件 | 说明 |
|---|---|
| `style.css` | 全站设计变量与组件样式（色板、字号阶梯、圆角、间距、照片占位块、后台表格/标签/分页） |
| `01-front-home.html` … `07-admin-order.html` | 每张稿一个文件，双击用浏览器打开即可看到原稿 |
| `probe.html` | 字体可用性探针（验证 `Noto Serif SC` / `Noto Sans SC` / `Georgia` 是否命中） |

改稿流程：改 `src\style.css` 里的 CSS 变量（`--gold` 换主色、`--ink` 换背景）→ 浏览器刷新 → 满意了截图。

## 怎么把这些稿子变成真代码

稿子是 HTML 结构，映射到 Vue 只需拆文件，类名和样式可以整段搬：

| 稿子里的块 | 落成的组件 |
|---|---|
| `.hero` | `web\src\components\front\HeroBanner.vue` |
| `.dish-card` | `components\front\DishCard.vue`（接 `props: dish`） |
| `.catbar` / `.side-cat` | `components\front\CategoryNav.vue` |
| `.chef` | `components\front\ChefNote.vue` |
| `.buybar` | `views\front\DishDetail.vue` 底部条 |
| `.admin` + `.aside` | 替换 `views\layout\AdminLayout.vue` 的侧栏与配色 |
| `.stat` / `.card` | `views\admin\Home.vue`（M7 看板）的指标卡 |
| `table` + `.tag` + `.pager` | 复制 `views\admin\UserManage.vue` 的三段式结构，套这里的表格样式 |

`style.css` 建议拆成 `web\src\styles\theme.css`（变量 + 基础）和组件内 `<style scoped>`，并在 `main.js` 里 `import './styles/theme.css'`（放在 Element Plus 之后）。

## 已知局限

- 静态稿，没有交互（hover / 滚动动效 / 抽屉滑出都只是定格）
- 照片是 CSS 生成的暗调占位块，不是真实菜品图
- 图表是手写 SVG，正式实现用 echarts（依赖已在 `package.json`），只需把配色换成 `--gold` 系列
- 字体依赖本机已安装的 `Noto Serif SC`（已实测存在）；换机器若缺失会 fallback 到宋体/Georgia，观感会掉一档，正式项目请把字体文件放到 `web\public\fonts\`
