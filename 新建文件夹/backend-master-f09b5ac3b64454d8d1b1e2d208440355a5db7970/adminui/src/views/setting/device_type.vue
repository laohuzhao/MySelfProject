<template>
  <div class="app-container calendar-list-container">
    <div class="filter-container" style="padding-bottom: 15px;">
      <el-button class="filter-item" type="primary" icon="plus" @click="handleAdd">新增</el-button>
    </div>
    
    <el-table :data="list" v-loading.body="listLoading" element-loading-text="拼命加载中" border fit highlight-current-row style="width: 100%">
      <el-table-column label="代码" width="150">
        <template scope="scope">{{scope.row.code}}</template>
      </el-table-column>
      <el-table-column label="名称">
        <template scope="scope">{{scope.row.name}}</template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template scope="scope">{{scope.row.read_time}}
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
    <el-dialog :title="dlgTitle[dlgStatus]" :visible.sync="dlgEditShow" :close-on-click-modal="false">
      <el-form class="small-space" :model="temp" ref="temp" :rules="rules" label-width="130px">
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
    var validateHex4 = (rule, value, callback) => {
      if (/^[0-9A-F]{4}$/.test(value)) {
        callback();
      } else {
        callback(new Error('行业代码范围为0000~FFFF'));
      }
    }

    return {
      list: null,
      listLoading: true,
      total: null,
      listQuery: {
        type: 'dtype'
      },
      dlgStatus: '',
      dlgTitle: {
        view: '查看行业',
        update: '编辑行业',
        create: '创建行业'
      },
      dlgEditShow: false,
      temp: {
        type: 'dtype',
        op: 1,
        code: '',
        name: ''
      },
      rules: {
        code: [
          { required: true, message: '请输入行业代码', trigger: 'blur' },
          { validator: validateHex4, trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.fetchData();
  },
  methods: {
    resetTemp() {
      this.temp = {
        type: 'dtype',
        op: 1,
        code: '',
        name: ''
      }
      if (this.$refs['temp'] != undefined) {
        this.$refs['temp'].resetFields()
      }
    },
    fetchData() {
      this.listLoading = true;
      getDictList(this.listQuery).then(response => {
        this.total =response.data.total
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
      this.dlgEditShow = true
    },
    handleEdit(row) {
      this.resetTemp()
      this.temp = {
        type: 'dtype',
        op: 0,
        code: row.code,
        name: row.name
      }
      this.dlgStatus = 'update'
      this.dlgEditShow = true
    },
    handleDelete(row) {
      self = this;
      this.$confirm('您是否确认删除 - ' + row.code + ',' + row.name + '？')
        .then(_ => {
          deleteDict({
            type: 'dtype',
            code: row.code
          }).then(response => {
            self.$message({ message: response.data, type: 'info' });
            self.fetchData()
          });
        })
        .catch(_ => {
        });
    },
    handleSave() {
      this.$refs['temp'].validate((valid) => {
        if (valid) {
          saveDict(this.temp).then(response => {
            this.fetchData()
            this.$message({ message: response.data, type: 'info' })
            this.dlgEditShow = false
          }, error => {});
        }
      });
    }
  }
};
</script>
