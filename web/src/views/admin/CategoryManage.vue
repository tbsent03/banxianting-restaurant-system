<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width:100%">
      <el-card>
        <el-form ref="searchFormComponents" :model="searchForm" inline>
            <el-form-item label="分类名称" prop="name">
              <el-input v-model="searchForm.name" clearable></el-input>
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
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column property="id" label="ID"></el-table-column>
          <el-table-column property="name" label="分类名称"></el-table-column>
          <el-table-column property="nameEn" label="英文名称"></el-table-column>
          <el-table-column property="sort" label="排序"></el-table-column>
          <el-table-column property="createTime" label="创建时间" width="200"></el-table-column>
          <el-table-column property="status" label="状态"></el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button type="primary" :icon="Edit" @click="edit(scope.row)">编辑</el-button>
              <el-button type="danger"  :icon="Delete" @click="deleteOne(scope.row)">删除</el-button>
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
        <el-form-item label="分类名称" prop="name" :rules="[{required: true,message:'不能为空',trigger:['blur','change']}]">
          <el-input v-model="formData.name"></el-input>
        </el-form-item>
        <el-form-item label="英文名称" prop="nameEn">
          <el-input v-model="formData.nameEn"></el-input>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="formData.sort" :min="0" :max="999"></el-input-number>
        </el-form-item>
        <el-form-item label="状态" prop="status" :rules="[{required: true,message:'不能为空',trigger:['blur','change']}]">
          <el-radio-group v-model="formData.status">
            <el-radio label="启用">启用</el-radio>
            <el-radio label="禁用">禁用</el-radio>
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
import {Search,Refresh,Plus,Delete,Edit,Check,Close,RefreshLeft} from '@element-plus/icons-vue'
import {ref, toRaw} from "vue";
import request from "@/utils/http.js"
import MyUpload from "../../components/MyUpload.vue";
import {ElMessageBox, ElMessage} from "element-plus";

const searchFormComponents = ref()

//搜索参数
const searchForm = ref({
  name:undefined,
  status:undefined
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
const statusList=ref(['启用','禁用'])

const listData = ref([
  {
    username:"张三",
    nickname:"小三"
  }
])
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
  request.get("/category/page",{
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
  formData.value = {}
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
 * @param rows
 */
function batchDelete(rows){
    if(!rows){
      //如果没有传入rows，则使用已经被选中的数据
      rows = selectionRows.value
    }

    //从数据中获取id列表
    const ids = rows.map(item => item.id)
    //es6 模板字符串
    ElMessageBox.confirm(
      `此操作将永久删除id为[${ids}]的数据，是否继续?`,
      'Warning',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        center: true,
      }
  )
      .then(() => {
        //向客户端发送删除请求
        request.delete("/category/delBatch",{data:ids}).then(()=>{
          ElMessage({
            type: 'success',
            message: '删除成功',
          })
          //重新获取数据
          getPageList()
        })

      })
      .catch(() => {
        ElMessage({
          type: 'info',
          message: '取消删除',
        })
        //取消按钮时自动取消表格选中
        tableComponents.value.clearSelection()
      })

}

/**
 * 单个删除
 */
function deleteOne(row){
  batchDelete([row])
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
    const url = formData.value.id ? "/category/update" : "/category/add"

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

function resetPassword(row){
  request.post("/common/resetPassword?type=USER&id="+row.id).then(res=>{
    ElMessage({
      message:res.message,
      type:'success'
    })
  })
}

</script>