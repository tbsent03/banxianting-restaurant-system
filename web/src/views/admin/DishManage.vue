<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width:100%">
      <el-card>
        <el-form ref="searchFormComponents" :model="searchForm" inline>
            <el-form-item label="菜品名称" prop="name">
              <el-input v-model="searchForm.name" placeholder="请输入菜名" clearable></el-input>
            </el-form-item>
          <el-form-item label="所属分类" prop="categoryId">
            <el-select v-model="searchForm.categoryId" placeholder="全部分类" clearable style="width: 150px">
              <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id"/>
            </el-select>
          </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="searchForm.status" placeholder="请选择" clearable filterable style="width: 150px">
                <el-option :label="item"  :key="item" :value="item" v-for="item in statusList"/>
              </el-select>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" :icon="Search" @click="search">搜索</el-button>
              <el-button type="" :icon="Refresh" @click="resetSearch">重置</el-button>
            </el-form-item>
        </el-form>
        <el-space>
          <el-button type="primary" :icon="Plus" @click="add">新增</el-button>
          <el-button type="danger" :disabled="selectionRows.length<=0"  :icon="Delete" @click="batchDelete">批量删除</el-button>
        </el-space>
      </el-card>
      <el-card>
        <el-table :data="listData"
                  style="width: 100%"
                  @selection-change="selectionChange"
                  ref="tableComponents"
                  border
        >
          <el-table-column property="coverUrl" label="主图" width="100">
            <template #default="scope">
              <el-image
                  style="width: 60px; height: 60px; border-radius: 6px;"
                  :src="scope.row.coverUrl"
                  :preview-src-list="[scope.row.coverUrl]"
                  preview-teleported
                  fit="cover"
              />
            </template>
          </el-table-column>
          <el-table-column label="排序" width="120" align="center">
            <template #default="scope">
              <el-input-number
                  v-model="scope.row.sort"
                  :min="0"
                  :max="999"
                  size="small"
                  controls-position="right"
                  @change="updateSort(scope.row)"
                  style="width: 90px;"
              />
            </template>
          </el-table-column>
          <el-table-column property="name" label="菜品名称"></el-table-column>
          <el-table-column property="categoryName" label="所属分类"></el-table-column>
          <el-table-column property="price" label="价格(¥)">
            <template #default="scope"> ¥ {{ scope.row.price }} </template>
          </el-table-column>
          <el-table-column property="stock" label="库存"></el-table-column>
          <el-table-column property="status" label="状态"></el-table-column>
          <el-table-column label="招牌" width="100" align="center">
            <template #default="scope">
              <el-switch
                  v-model="scope.row.isSignature"
                  :active-value="1"
                  :inactive-value="0"
                  @change="(val) => toggleSignature(scope.row, val)"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button type="primary" :icon="Edit" @click="edit(scope.row)">编辑</el-button>
              <el-button type="danger" :icon="Delete" @click="deleteOne(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div style="margin-top: 20px">
          <el-pagination
            @current-change = "currentChange"
            @size-change="sizeChange"
            :page-size="pageInfo.pageSize"
            :current-page="pageInfo.pageNum"
            background
            layout="total,sizes,prev,pager,next"
            :total="pageInfo.total"
            />
        </div>
      </el-card>
    </el-space>
    <el-dialog
    v-model="dialogOpen"
    v-if="dialogOpen"
    :title="formData.id?'编辑':'新增'"
    width="500"
    >
      <el-form ref="formRef" :model="formData" label-width="100">
        <el-form-item label="菜品名称" prop="name" :rules="[{required: true,message:'不能为空',trigger:'blur'}]">
          <el-input v-model="formData.name"></el-input>
        </el-form-item>
        <el-form-item label="英文名称" prop="nameEn">
          <el-input v-model="formData.nameEn"></el-input>
        </el-form-item>
        <el-form-item label="所属分类" prop="categoryId" :rules="[{required: true,message:'请选择分类',trigger:'change'}]">
          <el-select v-model="formData.categoryId" placeholder="请选择">
            <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="菜品主图" prop="coverUrl" :rules="[{required: true,message:'请上传图片',trigger:'change'}]">
          <MyUpload type="imageCard" :limit="1" :files="formData.coverUrl" @setFiles="formData.coverUrl = $event" v-if="dialogOpen" />
        </el-form-item>
        <el-form-item label="一句话描述" prop="description">
          <el-input type="textarea" v-model="formData.description" placeholder="例如：炭烤M9和牛..."></el-input>
        </el-form-item>
        <el-form-item label="主要食材" prop="ingredients">
          <el-input v-model="formData.ingredients" placeholder="例如：和牛、黑松露、芦笋"></el-input>
        </el-form-item>
        <el-form-item label="辣度" prop="spicyLevel">
          <el-input-number v-model="formData.spicyLevel" :min="0" :max="3"></el-input-number>
          <span style="font-size: 12px; color: #999; margin-left: 10px;">0不辣, 1微辣, 2中辣, 3特辣</span>
        </el-form-item>
        <el-form-item label="推荐配酒" prop="pairing">
          <el-input v-model="formData.pairing" placeholder="例如：波尔多干红"></el-input>
        </el-form-item>
        <el-form-item label="价格(¥)" prop="price" :rules="[{required: true,message:'不能为空',trigger:'blur'}]">
          <el-input-number v-model="formData.price" :precision="2" :step="10" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="库存" prop="stock" :rules="[{required: true,message:'不能为空',trigger:'blur'}]">
          <el-input-number v-model="formData.stock" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio label="上架">上架</el-radio>
            <el-radio label="下架">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" :icon="Check" @click="submit">提交</el-button>
        <el-button type="" :icon="Close" @click="closeDialog">取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref,onMounted,toRaw } from "vue";
import {Search,Refresh,Plus,Delete,Edit,Check,Close,RefreshLeft} from '@element-plus/icons-vue'
import request from "@/utils/http.js"
import MyUpload from "../../components/MyUpload.vue";
import {ElMessageBox, ElMessage} from "element-plus";

const categoryList = ref([])
const searchFormComponents = ref()

onMounted(() => {
  // 页面加载时拉取分类列表
  getPageList()
  request.get('/category/list').then(res => {
    if(res.code === 200){
      categoryList.value = res.data
    }
  });
});

//搜索参数
const searchForm = ref({
  name:undefined,
  status:undefined,
  categoryId:undefined
})

//分页
const pageInfo = ref({
  //当前页
  pageNum:1,
  //每页条数
  pageSize:10,
  //总条数
  total:0
})

//状态
const statusList=ref(['上架','下架'])

const listData = ref([])
//新增编辑
const formData = ref({})

//控制弹窗的开关
const dialogOpen = ref(false)

const formRef = ref()

//获取选中的数据
const selectionRows = ref([])

const tableComponents = ref()

getPageList()
function getPageList(){
  //向后端发起请求 携带参数 分页参数+搜索参数
  request.get("/dish/page",{
    params:Object.assign(toRaw(searchForm.value),toRaw(pageInfo.value))
  }).then(res => {
    //获取列表
    listData.value = res.data.list
    //获取总记录条数
    pageInfo.value.total = res.data.total
  })
}



/**
 * 搜索
 */
function search(){
  //当用户搜索时，分页需要重置为第一页
  pageInfo.value.pageNum = 1
  //调用分页方法
  getPageList()

}

/**
 * 重置搜索
 */
function resetSearch(){
  searchFormComponents.value.resetFields()
  getPageList()

}

/**
 * 选择分页
 * @param num
 */
function currentChange(num){
  pageInfo.value.pageNum = num;
  getPageList()
}

/**
 * 改变分页数量
 * @param num
 */
function sizeChange(num){
  pageInfo.value.pageSize = num
  getPageList()
}

/**
 * 新增
 */
function add(){
  //清空值
  formData.value = {
    isSignature: 0,
    status:'上架'
  }
  // formRef.value.resetFields()
  //点击按钮，打开弹窗
  dialogOpen.value = true

}

/**
 * 编辑
 * @param row
 */
function edit(row){
  formData.value = Object.assign({},row)
  dialogOpen.value = true

}

/**
 * 选中
 */
function  selectionChange(rows){
  selectionRows.value = rows
}

/**
 * 批量删除
 */
function batchDelete(rows){
  if(!rows){
    // 如果没有传入 rows，则使用表格中勾选的数据
    rows = selectionRows.value
  }

  // 从数据中获取 id 列表
  const ids = rows.map(item => item.id)

  ElMessageBox.confirm(
      `此操作将永久删除id为[${ids}]的数据，是否继续?`,
      'Warning',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        center: true,
      }
  ).then(() => {
    // 向客户端发送删除请求，🔥 注意这里的 (res)
    request.delete("/dish/delBatch",{data:ids}).then((res) => {
      if (res.code === 200) {
        ElMessage({
          type: 'success',
          message: '删除成功',
        })
        // 重新获取数据
        getPageList()
        // 清理表格的勾选状态
        tableComponents.value.clearSelection()
      } else {
        ElMessage.error(res.msg || "删除失败")
      }
    })
  })
      .catch(() => {
        ElMessage({
          type: 'info',
          message: '取消删除',
        })
      })
}

/**
 * 单个删除
 */
function deleteOne(row){
  // 直接调用批量删除，传入一个只包含这一行数据的数组
  batchDelete([row])
}

/**
 * 切换招牌状态
 */
function toggleSignature(row, val) {
  // 如果 val 是 1，说明用户想把它设为招牌，需要校验数量
  if (val === 1) {
    // 统计当前页或者全表有多少个招牌菜
    // 注意：因为目前分页可能只查了当前页，最严谨的做法是向后端查一下总数
    // 我们这里先用一个简单的请求去查一下总共有几个招牌
    request.get('/dish/page', {
      params: { pageNum: 1, pageSize: 100, isSignature: 1 } // 查所有招牌
    }).then(res => {
      if(res.code === 200) {
        const currentSignatures = res.data.list.length;
        if (currentSignatures >= 3) {
          // 超过3个，拦截！
          ElMessage.error('招牌菜已满！最多只能设置 3 道');
          // 把开关拨回去
          row.isSignature = 0;
          return;
        } else {
          // 没超过3个，发送更新请求
          updateSignature(row);
        }
      }
    })
  } else {
    // 如果从招牌取消，直接发请求
    updateSignature(row);
  }
}

// 发送更新请求的辅助函数
function updateSignature(row) {
  request.put('/dish/update', row).then(res => {
    if(res.code === 200) {
      ElMessage.success(row.isSignature === 1 ? '已设为招牌' : '已取消招牌');
      // 如果希望首页立刻能看到更新，可以在这里什么都不做，首页刷新就会变
    } else {
      ElMessage.error(res.msg || '操作失败');
    }
  })
}

/**
 * 修改排序权重
 */
function updateSort(row) {
  // 直接调用后端的更新接口（因为我们之前写 update 接口时，它接收的是整个 Dish 对象）
  request.put('/dish/update', row).then(res => {
    if(res.code === 200) {
      ElMessage.success('排序已更新');
      // 关键：更新成功后，重新拉取表格数据，让列表立刻按照新排序展示
      getPageList();
    } else {
      ElMessage.error(res.msg || '排序更新失败');
    }
  })
}

/**
 * 数据新增编辑
 */
function submit(){
  formRef.value.validate((valid) =>{
    if(!valid){
      ElMessage({
        message:'验证失败，请检查表单！',
        type:"warning"
      })
      return
    }
    //三元表达式
    //请求类型
    const requestMethod = formData.value.id ? request.put : request.post
    //请求路径
    const url = formData.value.id ? "/dish/update" : "/dish/add"

    requestMethod(url,formData.value).then(res => {
      dialogOpen.value = false
      ElMessage({
        message:res.msg,
        type:"success"
      })
      //重新获取列表
      getPageList()
    })
  })
}
function closeDialog(){
  dialogOpen.value= false
}



</script>