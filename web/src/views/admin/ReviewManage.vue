<template>
  <div class="review-manage">
    <div class="breadcrumb-bar">内容 / 评价管理</div>
    <h2 class="page-title">评价管理</h2>

    <el-card shadow="never" style="margin-top: 15px;">
      <el-table :data="listData" style="width: 100%" stripe>
        <el-table-column prop="userName" label="评价用户" width="120"></el-table-column>
        <el-table-column prop="orderNo" label="关联订单号" width="200"></el-table-column>
        <el-table-column label="评分" width="160">
          <template #default="scope">
            <div style="display: flex; align-items: center; gap: 8px;">
              <el-rate
                  v-model="scope.row.score"
                  disabled
                  text-color="#D5A958"
                  size="small"
              ></el-rate>
              <span style="color: #D5A958; font-size: 14px; font-weight: bold; margin-top: 2px;">
                {{ scope.row.score }}.0
              </span>
            </div>          </template>
        </el-table-column>
        <el-table-column prop="content" label="评价内容" show-overflow-tooltip></el-table-column>
        <el-table-column prop="createTime" label="评价时间" width="180"></el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="openReplyDialog(scope.row)">查看/回复</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top: 20px; text-align: right;">
        <el-pagination background layout="total, prev, pager, next" :total="pageInfo.total"
                       :page-size="pageInfo.pageSize" :current-page="pageInfo.pageNum" @current-change="currentChange" />
      </div>
    </el-card>

    <!-- 回复弹窗 -->
    <el-dialog v-model="replyDialogVisible" title="评价详情与回复" width="500px">
      <div class="review-detail-box" v-if="currentReview">
        <div style="display: flex; align-items: center; gap: 10px; margin-bottom: 15px;">
          <strong>用户评分：</strong>
          <el-rate v-model="currentReview.score" disabled text-color="#D5A958"></el-rate>
          <span style="color: #D5A958; font-weight: bold; font-size: 16px;">{{ currentReview.score }}.0</span>
        </div>
        <p><strong>顾客评价：</strong> {{ currentReview.content || '该用户未填写文字评价' }}</p>
        <p style="color: #999; font-size: 12px;">评价时间：{{ currentReview.createTime }}</p>

        <el-divider>商家回复</el-divider>

        <el-input
            v-model="replyContent"
            type="textarea"
            :rows="4"
            placeholder="请输入您的回复，回复将展示给顾客..."
        ></el-input>
      </div>
      <template #footer>
        <el-button @click="replyDialogVisible = false">取消</el-button>
        <el-button type="primary" class="search-btn" @click="submitReply">提交回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from "@/utils/http.js"
import { ElMessage } from 'element-plus'

const listData = ref([])
const pageInfo = ref({ pageNum: 1, pageSize: 10, total: 0 })

const replyDialogVisible = ref(false)
const currentReview = ref(null)
const replyContent = ref('')

onMounted(() => {
  getPageList()
})

function getPageList() {
  request.get('/review/page', {
    params: { pageNum: pageInfo.value.pageNum, pageSize: pageInfo.value.pageSize }
  }).then(res => {
    if (res && res.data) {
      listData.value = res.data.list || []
      pageInfo.value.total = res.data.total || 0
    }
  })
}

function currentChange(num) {
  pageInfo.value.pageNum = num
  getPageList()
}

function openReplyDialog(row) {
  currentReview.value = row
  replyContent.value = row.reply || '' // 如果之前回复过，回显出来
  replyDialogVisible.value = true
}

function submitReply() {
  if (!replyContent.value.trim()) {
    return ElMessage.warning('请输入回复内容')
  }

  request.post('/review/reply', {
    id: currentReview.value.id,
    reply: replyContent.value
  }).then(res => {
    if (res && res.code === 200) {
      ElMessage.success('回复成功')
      replyDialogVisible.value = false
      getPageList()
    }
  })
}
</script>

<style scoped>
.breadcrumb-bar { width: 100%; background-color: #fff; padding: 15px 30px; font-size: 14px; color: #666; border-bottom: 1px solid #eee; box-sizing: border-box; margin-bottom: 20px; }
.page-title { font-size: 24px; color: #333; margin-bottom: 20px; font-weight: 600; }
.review-detail-box p { line-height: 1.8; margin-bottom: 10px; color: #333; }
.search-btn { background-color: #C6A15B !important; color: #101010 !important; border-color: #C6A15B !important; font-weight: bold;}
</style>