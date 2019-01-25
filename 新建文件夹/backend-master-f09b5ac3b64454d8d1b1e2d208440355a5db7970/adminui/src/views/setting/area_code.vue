<template>
  <div class="app-container calendar-list-container">
    <div class="filter-container" style="padding-bottom: 15px;">
      <el-button class="filter-item" type="primary" icon="plus" @click="handleAdd">新增省份</el-button>
    </div>
    
    <el-table :data="list" v-loading.body="listLoading" element-loading-text="拼命加载中" border fit highlight-current-row style="width: 100%">
      <el-table-column type="expand" style="background-color:red;">
        <template scope="scope">
          <el-table :data="scope.row.children" border fit highlight-current-row style="width: 100%; margin-bottom:5px;">
            <el-table-column label="城市代码" width="150">
              <template scope="scope">{{scope.row.code}}</template>
            </el-table-column>
            <el-table-column label="城市名称">
              <template scope="scope">{{scope.row.label}}</template>
            </el-table-column>
            <el-table-column label="操作" width="180">
              <template scope="scope">
                <el-button type="text" style="margin-left:0px;" @click="handleEdit(scope.row)">编辑</el-button>
                &nbsp;&nbsp;&nbsp;
                <el-button type="text" style="margin-left:0px;" @click="handleDelete(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
  
          <el-button @click="handleAddSub(scope.row)">新增城市【{{scope.row.label}}】</el-button>
        </template>
      </el-table-column>
      <el-table-column label="省份代码" width="150">
        <template scope="scope">{{scope.row.code}}</template>
      </el-table-column>
      <el-table-column label="省份名称">
        <template scope="scope">{{scope.row.label}}</template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template scope="scope">
          <el-button type="text" style="margin-left:0px;" @click="handleEdit(scope.row)">编辑</el-button>
          &nbsp;&nbsp;&nbsp;
          <el-button type="text" style="margin-left:0px;" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div v-show="!listLoading" class="pagination-container" style="padding-top: 15px;">
      <el-pagination layout="total" :total="total">
      </el-pagination>
    </div>

    <!-- 新增/编辑 -->
    <el-dialog :title="dlgTitle" :visible.sync="dlgEditShow" :close-on-click-modal="false">
      <el-form class="small-space" :model="temp" ref="temp" label-width="130px">
        <el-form-item label="代码" prop="code">
          <span v-if="dlgStatus == 'update'">{{temp.code}}</span>
          <el-input v-else v-model="temp.code"></el-input>
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <span v-if="dlgStatus == 'view'">{{temp.name}}</span>
          <el-input v-else v-model="temp.name"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dlgEditShow = false">返 回</el-button>
        <el-button type="primary" @click="handleSave">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { parseTime } from '@/utils/index'
import { getDictList, saveDict, deleteDict } from '@/api/setting'

export default {
  data() {
    return {
      list: null,
      listLoading: true,
      total: null,
      listQuery: {
        type: 'dtype'
      },
      dlgStatus: '',
      dlgTitle: '',
      dlgEditShow: false,
      temp: {
        type: 'areacode',
        op: 1,
        id: '',
        code: '',
        name: '',
        parent_id: '',
        parent_code: '',
        parent_name: ''
      }
    }
  },
  created() {
    this.fetchData();
  },
  methods: {
    resetTemp() {
      this.temp = {
        type: 'areacode',
        op: 1,
        id: '',
        code: '',
        name: '',
        parent_id: '',
        parent_code: '',
        parent_name: ''
      }
    },
    fetchData() {
      this.listLoading = true;
      getDictList({ type: 'areacode_tree', parent_code: '' }).then(response => {
        this.total = response.data.total
        this.list = response.data.items
        this.listLoading = false
      }, error => {});
    },
    handleFilter() {
      this.fetchData()
    },
    handleAdd() {
      this.resetTemp()
      this.dlgStatus = 'create'
      this.dlgTitle = '创建新省份'
      this.dlgEditShow = true
    },
    handleAddSub(row) {
      this.resetTemp()

      this.temp.parent_id = row.id
      this.temp.parent_code = row.code
      this.temp.parent_name = row.label
      
      this.dlgStatus = 'create'
      this.dlgTitle = '创建新城市'
      this.dlgEditShow = true
    },
    handleEdit(row) {
      this.temp = {
        type: 'areacode',
        op: 0,
        id: row.id,
        code: row.code,
        name: row.label
      }
      this.dlgStatus = 'update'
      this.dlgTitle = '编辑 - ' + row.code + ',' + row.label
      this.dlgEditShow = true
    },
    handleDelete(row) {
      var self = this
      var id = row.id
      var code = row.code
      var name = row.label
      
      this.$confirm('您是否确认删除 - ' + name + '？')
        .then(_ => {
          deleteDict({
            type: 'areacode',
            id: id
          }).then(response => {
            self.$message({ message: response.data, type: 'info' });
            self.fetchData()
          });
        })
        .catch(_ => {
        });
    },
    handleSave() {
      saveDict(this.temp).then(response => {
        this.fetchData()
        this.$message({ message: response.data, type: 'info' })
        this.dlgEditShow = false
      }, error => {});
    }
  }
};
</script>
