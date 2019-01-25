<template>
  <div class="app-container calendar-list-container">
    <div class="filter-container" style="padding-bottom: 15px;">
      <el-button class="filter-item" type="primary" icon="plus" @click="handleNew">新增人员</el-button>
    </div>

    <el-table :data="list" v-loading.body="listLoading" element-loading-text="拼命加载中" border fit highlight-current-row style="width: 100%">
      <el-table-column label="账号" width="150">
        <template scope="scope">{{scope.row.name}}</template>
      </el-table-column>
      <el-table-column label="手机号码" width="150" align="center">
        <template scope="scope">{{scope.row.mobile}}</template>
      </el-table-column>
      <el-table-column label="角色">
        <template scope="scope">{{scope.row.role_id_}}</template>
      </el-table-column>
      <el-table-column label="添加时间" width="170">
        <template scope="scope">{{scope.row.add_time}}</template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template scope="scope">
          <el-button type="text" size="small" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
          <el-button type="text" size="small" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div v-show="!listLoading" class="pagination-container" style="padding-top: 15px;">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page.sync="listQuery.page"
        :page-sizes="[10,20,30, 50]" :page-size="listQuery.limit" layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </div>
    
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" :close-on-click-modal="false">
      <el-form class="small-space" :model="temp" ref="temp" :rules="rules" label-position="left" label-width="80px" style='margin-left:50px;'>
        <el-form-item label="登录账号" prop="name">
          <el-input v-model="temp.name"></el-input>
        </el-form-item>
        <el-form-item label="手机号码" prop="mobile">
          <el-input v-model="temp.mobile" ></el-input>
        </el-form-item>
        <el-form-item label="登录密码" prop="password">
          <el-input v-model="temp.password" type="password"></el-input>
        </el-form-item>
        <el-form-item label="所属角色" prop="role_id">
          <el-select v-model="temp.role_id" filterable placeholder="请选择" style="width:100%;">
            <el-option v-for="item in allRoles" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="handleSave">确 定</el-button>
        <el-button v-else type="primary" @click="handleSave">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import { getUserList, saveUser, deleteUser, selectRole } from '@/api/auth';
  
  export default {
    data() {
      var validatePassword = (rule, value, callback) => {
        if (this.temp.id === undefined) {
          if (value === '') {
            callback(new Error('请输入登录密码'));
          } else if (value.length < 4) {
            callback(new Error('登录密码长度必须大于4位'));
          } else {
            callback();
          }
        } else {
          callback();
        }
      };

      var validateMobile = (rule, value, callback) => {
        if (!(/^1\d{10}$/.test(value))) {
          callback(new Error('请输入正确的手机号码'));
        } else {
          callback();
        }
      };

      return {
        list: null,
        total: null,
        listLoading: true,
        listQuery: {
          page: 1,
          limit: 10
        },
        dialogFormVisible: false,
        dialogStatus: '',
        textMap: {
          update: '编辑',
          create: '创建'
        },
        allRoles: [],
        temp: {
          id: undefined,
          name: '',
          mobile: '',
          password: '',
          role_id: ''
        },
        rules: {
          name: [
            { required: true, message: '请输入账号', trigger: 'blur' }
          ],
          mobile: [
            { required: true, message: '请输入手机号码', trigger: 'blur' },
            { validator: validateMobile, trigger: 'blur' }
          ],
          password: [
            { validator: validatePassword, trigger: 'blur' }
          ]
        }
      }
    },
    created() {
      this.fetchData();
      selectRole({ type: 'SYS' }).then(response => {
        if (response.code === 20000) {
          for (const i = 0; i < response.data.items.length; i++) {
            const item = response.data.items[i];
            this.allRoles.push({ label: item.name, value: item.id });
          }
        }
      });
    },
    methods: {
      fetchData() {
        this.listLoading = true;
        getUserList(this.listQuery).then(response => {
          this.total = response.data.total;
          this.list = response.data.items;
          this.listLoading = false;
        })
      },
      handleSizeChange(val) {
        this.listQuery.limit = val;
        this.fetchData();
      },
      handleCurrentChange(val) {
        this.listQuery.page = val;
        this.fetchData();
      },
      resetTemp() {
        this.temp = {
          id: undefined,
          name: '',
          mobile: '',
          password: '',
          role_id: ''
        };
      },
      handleNew() {
        this.resetTemp();
        this.dialogStatus = 'create';
        this.dialogFormVisible = true;
      },
      handleEdit(index, row) {
        this.temp = {
          id: row.id,
          name: row.name,
          mobile: row.mobile,
          password: '',
          role_id: row.role_id
        };

        this.dialogStatus = 'update';
        this.dialogFormVisible = true;
      },
      handleDelete(index, row) {
        self = this;
        this.$confirm('您是否确认删除用户 - ' + row.name + ',' + row.mobile + '？')
          .then(_ => {
            deleteUser({
              id: row.id
            }).then(response => {
              if (response.code == 20000) {
                self.$message({ message: '删除成功', type: 'success' });
                self.fetchData();
              } else {
                self.$message({ message: response.data, type: 'error' });
              }
            });
          })
          .catch(_ => {
          });
      },
      handleCancel() {
        this.dialogFormVisible = false;
        this.resetTemp();
      },
      handleSave() {
        self = this;
        this.$refs['temp'].validate((valid) => {
          if (valid) {
            saveUser({
              id: self.temp.id,
              name: self.temp.name,
              mobile: self.temp.mobile,
              password: self.temp.password,
              role_id: self.temp.role_id
            }).then(response => {
              if (response.code == 20000) {
                self.$message({ message: '保存成功', type: 'success' });
                self.fetchData();
              } else {
                self.$message({ message: response.data, type: 'error' });
              }
              self.dialogFormVisible = false;
              self.$refs['temp'].resetFields();
            });
          } else {
            return false;
          }
        });
      }
    }
  };
</script>
