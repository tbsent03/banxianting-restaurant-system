<template>
  <div class="dashboard-container">
    <!-- 顶栏标题 -->
    <div class="header">
      <div>
        <h2>经营看板</h2>
        <p>Dashboard</p>
      </div>
      <div class="header-date">
        {{ currentDate }}
      </div>
    </div>

    <!-- 4个核心数据卡片 -->
    <div class="kpi-cards">
      <div class="kpi-card">
        <div class="kpi-title">今日营业额</div>
        <div class="kpi-value">¥ {{ data.todayRevenue || 0 }}</div>
        <div :class="['kpi-trend', revenueTrend.isUp ? 'up' : 'down']">
          {{ revenueTrend.isUp ? '↑' : '↓' }} {{ revenueTrend.percent }}% (¥{{ revenueTrend.diff }}) 相比昨日
        </div>
      </div>
      <div class="kpi-card">
        <div class="kpi-title">今日订单数</div>
        <div class="kpi-value">{{ data.todayOrderCount || 0 }} <span class="unit">单</span></div>
        <div :class="['kpi-trend', orderTrend.isUp ? 'up' : 'down']">
          {{ orderTrend.isUp ? '↑' : '↓' }} {{ orderTrend.percent }}% ({{ orderTrend.diff }}单) 相比昨日
        </div>
      </div>
      <div class="kpi-card">
        <div class="kpi-title">待接单提醒</div>
        <div class="kpi-value warning">{{ data.pendingOrderCount || 0 }} <span class="unit">单</span></div>
        <div class="kpi-trend warn">需尽快处理</div>
      </div>
      <div class="kpi-card">
        <div class="kpi-title">累计注册会员</div>
        <div class="kpi-value">{{ data.userCount || 0 }} <span class="unit">人</span></div>
        <div class="kpi-trend">总计</div>
      </div>
    </div>

    <div class="eco-card">
      <div class="eco-content">
        <div class="eco-icon">🌿</div>
        <div class="eco-info">
          <div class="eco-title">半闲庭 · 绿色环保贡献</div>
          <div class="eco-desc">
            本周累计响应“光盘行动” <strong>128</strong> 次，共节约食材约 <strong>15.6kg</strong>。感谢您与食客共同践行绿色餐饮。
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区域 (第一排) -->
    <div class="charts-row">
      <!-- 趋势折线图 -->
      <div class="chart-box large">
        <div class="chart-title">近 7 日营业额 <span class="unit-text">单位：元</span></div>
        <div ref="trendChartRef" style="width: 100%; height: 320px;"></div>
      </div>

      <!-- 分类占比饼图 -->
      <div class="chart-box small">
        <div class="chart-title">分类销售占比 <span class="unit-text">本月</span></div>
        <div ref="pieChartRef" style="width: 100%; height: 320px;"></div>
      </div>
    </div>

    <!-- 底部：热销 Top 5 -->
    <div class="chart-box bottom-box">
      <div class="chart-title">热销 Top 5 <span class="unit-text">近 7 日</span></div>
      <div ref="barChartRef" style="width: 100%; height: 300px;"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted,computed  } from 'vue'
import * as echarts from 'echarts'
import request from "@/utils/http.js"

const data = ref({})
//  计算营业额涨跌趋势
const revenueTrend = computed(() => {
  const today = Number(data.value.todayRevenue || 0)
  const yesterday = Number(data.value.yesterdayRevenue || 0)
  const diff = Math.abs(today - yesterday)

  if (yesterday === 0) {
    return { isUp: today >= 0, percent: today > 0 ? 100 : 0, diff: diff }
  }
  const percent = ((diff / yesterday) * 100).toFixed(1)
  return { isUp: today >= yesterday, percent, diff }
})

//  计算订单数涨跌趋势
const orderTrend = computed(() => {
  const today = Number(data.value.todayOrderCount || 0)
  const yesterday = Number(data.value.yesterdayOrderCount || 0)
  const diff = Math.abs(today - yesterday)

  if (yesterday === 0) {
    return { isUp: today >= 0, percent: today > 0 ? 100 : 0, diff: diff }
  }
  const percent = ((diff / yesterday) * 100).toFixed(1)
  return { isUp: today >= yesterday, percent, diff }
})
const trendChartRef = ref(null)
const pieChartRef = ref(null)
const barChartRef = ref(null)

// 顶部日期
const currentDate = new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })

let trendChart = null
let pieChart = null
let barChart = null

onMounted(() => {
  initData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if(trendChart) trendChart.dispose()
  if(pieChart) pieChart.dispose()
  if(barChart) barChart.dispose()
})

function handleResize() {
  trendChart?.resize()
  pieChart?.resize()
  barChart?.resize()
}

function initData() {
  request.get('/stat/overview').then(res => {
    if (res.code === 200) {
      data.value = res.data
      setTimeout(() => {
        renderTrendChart()
        renderPieChart()
        renderBarChart()
      }, 100)
    }
  })
}

// 渲染折线图（浅色主题）
function renderTrendChart() {
  trendChart = echarts.init(trendChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '15%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.value.trendDates || [],
      axisLine: { lineStyle: { color: '#E4E7ED' } },
      axisLabel: { color: '#909399' },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#EBEEF5' } },
      axisLabel: { color: '#909399' }
    },
    series: [{
      name: '营业额',
      type: 'line',
      smooth: true,
      itemStyle: { color: '#C6A15B' }, // 金色节点
      lineStyle: { width: 3 },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(198, 161, 91, 0.2)' },
          { offset: 1, color: 'rgba(198, 161, 91, 0)' }
        ])
      },
      data: data.value.trendRevenues || []
    }]
  }
  trendChart.setOption(option)
}

// 渲染饼图
function renderPieChart() {
  pieChart = echarts.init(pieChartRef.value)

  const pieData = (data.value.categoryRatio || []).map(item => {
    return {
      name: item.cat_name,
      value: Number(item.total_val) || 0
    }
  })

  const totalRevenue = pieData.reduce((sum, item) => sum + item.value, 0)
  const formattedRevenue = totalRevenue.toLocaleString('zh-CN', { minimumFractionDigits: 0, maximumFractionDigits: 0 })

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}<br/>¥ {c} ({d}%)',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#EBEEF5',
      textStyle: { color: '#303133', fontSize: 14 },
      extraCssText: 'box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1); border-radius: 6px;'
    },

    title: {
      text: '本月营收',
      subtext: `¥ ${formattedRevenue}`,
      // 👇 核心修复：让文字和 series 的 center 绝对对齐
      left: '50%',
      top: '45%',
      textAlign: 'center',
      textVerticalAlign: 'middle',

      textStyle: {
        color: '#909399',
        fontSize: 13
      },
      subtextStyle: {
        color: '#303133',
        fontSize: 22,
        fontWeight: 'bold',
        fontFamily: 'Georgia'
      },
      itemGap: 8
    },

    legend: {
      top: 'bottom',
      icon: 'circle',
      itemWidth: 8,
      itemHeight: 8,
      textStyle: { color: '#606266' }
    },

    color: ['#C6A15B', '#4B5943', '#788275', '#CBB799', '#8A8A8A'],

    series: [{
      name: '销售占比',
      type: 'pie',
      radius: ['55%', '75%'], // 稍微把内圈放大一点(55%)，防止文字贴着圆环
      center: ['50%', '45%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 5,
        borderColor: '#fff',
        borderWidth: 3
      },
      label: { show: false },
      data: pieData
    }]
  }
  pieChart.setOption(option)
}

// 渲染条形图
function renderBarChart() {
  barChart = echarts.init(barChartRef.value)

  // 适配新字段 dish_name 和 sales_num
  const names = (data.value.hotDishes || []).map(item => item.dish_name).reverse()
  const values = (data.value.hotDishes || []).map(item => item.sales_num).reverse()

  const option = {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '5%', containLabel: true },
    xAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: '#EBEEF5' } },
      axisLabel: { color: '#909399' }
    },
    yAxis: {
      type: 'category',
      data: names,
      axisLine: { lineStyle: { color: '#E4E7ED' } },
      axisLabel: { color: '#606266' },
      axisTick: { show: false }
    },
    series: [{
      name: '销量',
      type: 'bar',
      barWidth: '35%',
      itemStyle: { color: '#D5A958', borderRadius: [0, 4, 4, 0] },
      label: { show: true, position: 'right', color: '#C6A15B' },
      data: values
    }]
  }
  barChart.setOption(option)
}
</script>

<style scoped>
/*  核心修改：大面积留白与浅灰背景 👇 */
.dashboard-container {
  padding: 20px 30px;
  background-color: #F5F7FA; /* 浅灰底色 */
  min-height: calc(100vh - 60px);
  color: #303133;
}
.kpi-trend.up { color: #67C23A; } /* 上涨绿色 */
.kpi-trend.down { color: #F56C6C; } /*  下跌红色 */

.header {
  margin-bottom: 25px;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}
.header h2 { font-size: 24px; margin: 0; font-weight: bold; color: #303133; }
.header p { font-size: 12px; color: #909399; margin: 5px 0 0 0; letter-spacing: 2px; text-transform: uppercase;}
.header-date { font-size: 14px; color: #909399; }

/* 卡片样式变纯白 */
.kpi-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}
.kpi-card {
  background: #FFFFFF;
  border-radius: 8px;
  padding: 25px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05); /* 淡淡的阴影 */
  border: 1px solid #EBEEF5;
}
.kpi-title { font-size: 14px; color: #606266; margin-bottom: 15px; font-weight: 500; }
.kpi-value { font-size: 32px; font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', sans-serif; font-weight: bold; color: #303133; }
.kpi-value.warning { color: #E6A23C; }
.unit { font-size: 14px; color: #909399; font-weight: normal; margin-left: 5px; }

/* 趋势小字 */
.kpi-trend { margin-top: 15px; font-size: 12px; color: #909399; }
.kpi-trend.up { color: #67C23A; }
.kpi-trend.warn { color: #E6A23C; font-weight: bold; }

/* 图表容器 */
.charts-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}
.chart-box {
  background: #FFFFFF;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  border: 1px solid #EBEEF5;
}
.chart-box.large { flex: 2; }
.chart-box.small { flex: 1; }
.chart-box.bottom-box { width: 100%; box-sizing: border-box; }

/* 图表标题布局 */
.chart-title {
  font-size: 16px;
  color: #303133;
  margin-bottom: 20px;
  font-weight: bold;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.unit-text { font-size: 12px; color: #909399; font-weight: normal; }

/* 环保贡献卡片样式 */
.eco-card {
  /* 墨黑渐变底色 */
  background: linear-gradient(135deg, #1a1a1a 0%, #2a2a2a 100%);
  border-left: 4px solid #67C23A; /* 环保绿边 */
  border-radius: 8px;
  padding: 20px 25px;
  color: #fff;
  margin-bottom: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
}
.eco-content {
  display: flex;
  align-items: center;
  gap: 20px;
}
.eco-icon {
  font-size: 36px;
}
.eco-title {
  font-size: 16px;
  color: #67C23A; /* 绿色标题 */
  font-weight: bold;
  margin-bottom: 6px;
  letter-spacing: 1px;
}
.eco-desc {
  font-size: 13px;
  color: #CCCCCC;
}
.eco-desc strong {
  color: #D4AF37; /* 金色高亮数据 */
  font-size: 16px;
  margin: 0 4px;
}
</style>