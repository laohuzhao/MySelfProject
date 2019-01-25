<template>
  <div class="app-container calendar-list-container">
    <div class="filter-container" style="padding-bottom: 15px;">
      <el-input @keyup.enter.native="handleFilter" style="width: 200px;" class="filter-item" placeholder="客户名称或电话" v-model="listQuery.keyword">
      </el-input>
      <el-button class="filter-item" type="primary" icon="search" @click="handleFilter">搜索</el-button>
      <el-button v-if="auth.sys == true" class="filter-item" type="primary" icon="plus" @click="handleNew" :disabled="!auth.edit">新增</el-button>
    </div>
  
    <el-table :data="list" v-loading.body="listLoading" 
      @expand="rowExpand"
      element-loading-text="拼命加载中" border fit highlight-current-row style="width: 100%">
      <el-table-column type="expand">
        <template scope="scope">
          <el-table :data="scope.row.userList" v-loading.body="scope.row.userLoading" border fit highlight-current-row style="width: 100%; margin-bottom:5px;">
            <el-table-column label="账号" width="150">
              <template scope="scope">{{scope.row.name}}</template>
            </el-table-column>
            <el-table-column label="手机号码">
              <template scope="scope">{{scope.row.mobile}}</template>
            </el-table-column>
            <el-table-column label="角色">
              <template scope="scope">
                <span v-if="scope.row.custom == 1">管理员</span>
                <span v-else>{{scope.row.role_id_}}</span>
              </template>
            </el-table-column>
            <el-table-column label="添加时间" width="170">
              <template scope="scope">{{scope.row.add_time}}</template>
            </el-table-column>
            <el-table-column label="操作" width="180">
              <template scope="scope">
                <el-button v-if="scope.row.custom == 2" type="text" style="margin-left:0px;" @click="handleEditUser(scope.row)">编辑</el-button>
                &nbsp;&nbsp;&nbsp;
                <el-button v-if="scope.row.custom == 2 && user_type != 'CUSTOM_USER'" type="text" style="margin-left:0px;" @click="handleDeleteUser(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
  
          <el-button v-if="user_type=='SYS'||user_type=='CUSTOM'" @click="handleAddUser(scope.row)">新增账号</el-button>
        </template>
      </el-table-column>
      <el-table-column label="客户名称" width="200">
        <template scope="scope">{{scope.row.name}}</template>
      </el-table-column>
      <el-table-column label="联系人" width="70">
        <template scope="scope">{{scope.row.contact}}</template>
      </el-table-column>
      <el-table-column label="手机号码" width="90">
        <template scope="scope">{{scope.row.mobile}}</template>
      </el-table-column>
      <el-table-column label="公司地址">
        <template scope="scope">{{scope.row.address}}</template>
      </el-table-column>
      <el-table-column label="关联设备数" width="70" align="right">
        <template scope="scope">{{scope.row.device_count}}</template>
      </el-table-column>
      <el-table-column label="账号" width="100">
        <template scope="scope">{{scope.row.account}}</template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template scope="scope">
          <el-button v-if="auth.type == 0 || auth.type == 1" type="text" size="small" @click="handleEdit(scope.$index, scope.row, true)">查看</el-button>
          <el-button v-if="auth.type == 0 || auth.type == 1" type="text" size="small" :disabled="!auth.edit" @click="handleEdit(scope.$index, scope.row, false)">编辑</el-button>

          <el-button v-if="scope.row.status==20 && auth.sys == true" type="text" size="small" :disabled="!auth.edit" @click="handleChangeStatus(scope.row, '您是否确认要停用客户？', 'status', 40)">停用</el-button>
          <el-button v-else-if="auth.sys == true" type="text" size="small" :disabled="!auth.edit" @click="handleChangeStatus(scope.row, '您是否确认要启用客户？', 'status', 20)">启用</el-button>

          <el-button v-if="scope.row.sms_alarm==0 && auth.sys == true" type="text" size="small" :disabled="!auth.edit" @click="handleChangeStatus(scope.row, '您是否确认要启用短信？', 'sms', 1)">启用短信</el-button>
          <el-button v-else-if="auth.sys == true" type="text" size="small" :disabled="!auth.edit" @click="handleChangeStatus(scope.row, '您是否确认要停用短信？', 'sms', 0)">停用短信</el-button>
          
        </template>
      </el-table-column>
    </el-table>

    <div v-show="!listLoading" class="pagination-container" style="padding-top: 15px;">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page.sync="listQuery.page"
        :page-sizes="[10,20,30, 50]" :page-size="listQuery.limit" layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </div>

    <!-- 新增/编辑 -->
    <el-dialog :title="dlgTitle[dlgStatus]" :visible.sync="dlgEditShow" :close-on-click-modal="false">
      <el-form class="small-space" :model="temp" ref="temp" :rules="rules" label-width="130px">
        <el-form-item label="客户名称" prop="name">
          <span v-if="dlgStatus == 'view'">{{temp.name}}</span>
          <el-input v-else v-model="temp.name"></el-input>
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="联系人" prop="contact">
              <span v-if="dlgStatus == 'view'">{{temp.contact}}</span>
              <el-input v-else v-model="temp.contact" style="width:100%;"></el-input>
            </el-form-item>
            <el-form-item label="登录账号" prop="account">
              <span v-if="dlgStatus == 'view'">{{temp.account}}</span>
              <el-input v-else v-model="temp.account" style="width:100%;"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="mobile">
              <span v-if="dlgStatus == 'view'">{{temp.mobile}}</span>
              <el-input v-else v-model="temp.mobile" style="width:100%;"></el-input>
            </el-form-item>
            <el-form-item v-if="dlgStatus != 'view'" label="登录密码" prop="password" class="is-required">
              <el-input v-model="temp.password" type="password" style="width:100%;"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="公司地址" prop="address">
          <span v-if="dlgStatus == 'view'">{{temp.address}}</span>
          <el-input v-else v-model="temp.address"></el-input>
        </el-form-item>
        <el-form-item label="所属行业" style="display:none;">
          <el-select style="width:100%;" :disabled="dlgStatus=='view'" multiple clearable v-model="custom_type_" placeholder="请选择所属行业">
            <el-option v-for="item in deviceTypeList" :key="item.code" :label="item.name" :value="item.code">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="启用短信">
          <el-switch v-model="temp.sms_alarm_" on-text="" off-text="" @change="handlerSmsChange">
          </el-switch>&nbsp;&nbsp;&nbsp;<em>(该功能需要额外收费)</em>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button v-if="dlgStatus=='view'" @click="dlgEditShow = false">返 回</el-button>
        <el-button v-else @click="dlgEditShow = false">取 消</el-button>

        <el-button v-if="dlgStatus=='create'" type="primary" @click="handleSave">确 定</el-button>
        <el-button v-if="dlgStatus=='update'" type="primary" @click="handleSave">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="dlgUserTitle" :visible.sync="dlgUserShow" :close-on-click-modal="false">
      <el-form class="small-space" :model="user" ref="user" :rules="rules_user" label-position="left" label-width="80px" style='margin-left:50px;'>
        <el-form-item label="登录账号" prop="name">
          <el-input v-model="user.name"></el-input>
        </el-form-item>
        <el-form-item label="手机号码" prop="mobile">
          <el-input v-model="user.mobile" ></el-input>
        </el-form-item>
        <el-form-item label="登录密码" prop="password" class="is-required">
          <el-input v-model="user.password" type="password"></el-input>
        </el-form-item>
        <el-form-item v-if="user_type != 'CUSTOM_USER'" label="所属角色" prop="role_id">
          <el-select v-model="user.role_id" filterable placeholder="请选择" style="width:100%;">
            <el-option v-for="item in allRoles" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dlgUserShow = false">取 消</el-button>
        <el-button type="primary" @click="handleSaveUser">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<style>
.el-table .cell {
  padding-left: 3px;
  padding-right: 3px;
  font-size: 9px;
}
</style>

<script>
import { mapGetters } from 'vuex';
import { parseTime } from '@/utils';
import { getCustomList, saveCustom, changeCustomStatus } from '@/api/custom';
import { getDictList } from '@/api/setting'
import { getUserList, saveUser, deleteUser, selectRole } from '@/api/auth'

const workingStateOptions = [
    { key: '20', display_name: '正常' },
    { key: '40', display_name: '休止' }
];

export default {
  computed: {
    ...mapGetters([
      'name',
      'roles',
      'user_type'
    ])
  },
  data() {
    var validateMobile = (rule, value, callback) => {
      if (!(/^1\d{10}$/.test(value))) {
        callback(new Error('请输入正确的手机号码'));
      } else {
        callback();
      }
    };

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

    var validatePassword2 = (rule, value, callback) => {
      if (this.user.id === undefined) {
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

    return {
      list: null,
      listLoading: true,
      total: null,
      listQuery: {
        page: 1,
        limit: 10,
        keyword: undefined
      },
      custom_type_: [],
      deviceTypeList: [],
      dlgEditShow: false,
      dlgStatus: '',
      dlgTitle: {
        view: '查看',
        update: '编辑',
        create: '创建'
      },
      temp: {
        id: undefined,
        name: '',
        contact: '',
        mobile: '',
        address: '',
        account: '',
        password: '',
        sms_alarm: 0,
        sms_alarm_: false,
        custom_type_: undefined
      },
      rules: {
        name: [
          { required: true, message: '请输入客户名称', trigger: 'blur' }
        ],
        contact: [
          { required: true, message: '请输入联系人', trigger: 'blur' }
        ],
        mobile: [
          { required: true, message: '请输入手机号码', trigger: 'blur' },
          { validator: validateMobile, trigger: 'blur' }
        ],
        account: [
          { required: true, message: '请输入登录账号', trigger: 'blur' }
        ],
        password: [
          { validator: validatePassword, trigger: 'blur' }
        ]
      },
      // 权限
      auth: {
        sys: false,
        edit: true,
        type: -1
      },
      dlgUserTitle: '',
      dlgUserShow: false,
      rules_user: {
        name: [
          { required: true, message: '请输入账号', trigger: 'blur' }
        ],
        mobile: [
          { required: true, message: '请输入手机号码', trigger: 'blur' },
          { validator: validateMobile, trigger: 'blur' }
        ],
        password: [
          { validator: validatePassword2, trigger: 'blur' }
        ]
      },
      allRoles: [],
      user: {
        id: undefined,
        name: '',
        mobile: '',
        password: '',
        custom_id: '',
        role_id: ''
      }
    }
  },
  created() {
    if ('SYS' == this.user_type) {
      this.auth.sys = true
      this.auth.type = 0
    } else if ('CUSTOM' == this.user_type) {
      this.auth.type = 1
    } else if ('CUSTOM_USER' == this.user_type) {
      this.auth.type = 2
    }

    this.auth.edit = this.roles.indexOf('custom:edit') > -1
    
    this.fetchData()

    getDictList({type: 'dtype'}).then(response => {
      var items = response.data.items
      for (var a in items) {
        var item = items[a]
        this.deviceTypeList.push({
          code: item.code, name: item.name
        })
      }
    }, error => {})

    selectRole({ type: 'CUSTOM' }).then(response => {
      if (response.code === 20000) {
        for (const i = 0; i < response.data.items.length; i++) {
          const item = response.data.items[i];
          this.allRoles.push({ label: item.name, value: item.id });
        }
      }
    })
  },
  methods: {
    fetchData() {
      this.listLoading = true;

      getCustomList(this.listQuery).then(response => {
        this.total = response.data.total
        this.list = response.data.items.map(v => {
          v.userLoading = false
          v.userList = []
          v.userTotal = undefined
          return v
        })
        this.listLoading = false
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
    handleFilter() {
      this.fetchData();
    },
    rowExpand(row, expanded) {
      if (expanded) {
        row.userLoading = true
        getUserList({ custom_id: row.id }).then(response => {
          var items = response.data.items.map(v => {
            return v
          })
          row.userList = items
          row.userLoading = false
          row.userTotal = response.total
        })
      }
    },
    resetTemp() {
      this.custom_type_ = [];
      this.temp = {
        id: undefined,
        name: '',
        contact: '',
        mobile: '',
        address: '',
        account: '',
        password: '',
        sms_alarm: 0,
        sms_alarm_: false,
        custom_type_: undefined
      }
    },
    handleNew() {
      this.resetTemp();
      this.dlgStatus = 'create';
      this.dlgEditShow = true;
    },
    handleEdit(index, row, readonly) {
      this.custom_type_ = [];
      if (row.custom_type != undefined) {
        for (var a in row.custom_type) {
          this.custom_type_.push(row.custom_type[a].type_code)
        }
      }
      this.temp = {
        id: row.id,
        name: row.name,
        contact: row.contact,
        mobile: row.mobile,
        address: row.address,
        account: row.account,
        password: row.password,
        sms_alarm: row.sms_alarm,
        sms_alarm_: false,
        custom_type_: undefined
      }
      if (this.$refs['temp'] != undefined) {
        this.$refs['temp'].resetFields();
      }
      if (row.sms_alarm === 1) {
        this.temp.sms_alarm_ = true;
      } else {
        this.temp.sms_alarm_ = false;
      }
      this.dlgStatus = readonly ? 'view' : 'update';
      this.dlgEditShow = true;
    },
    handlerSmsChange(v) {
      if (v) {
        this.$confirm('启用短信需要付费，您确定启用吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.temp.sms_alarm_ = v
        }).catch(() => {
          this.temp.sms_alarm_ = false
        });
      }
    },
    handleSave() {
      self = this;
      this.$refs['temp'].validate((valid) => {
        if (valid) {
          var params = self.temp;
          if (params.sms_alarm_) {
            params.sms_alarm = 1;
          } else {
            params.sms_alarm = 0;
          }
          params.custom_type_ = this.custom_type_.join(',');
          saveCustom(params).then(response => {
            if (response.code == 20000) {
              self.$message({ message: '保存成功', type: 'success' });
              self.fetchData();
            } else {
              self.$message({ message: response.data, type: 'error' });
            }
            self.dlgEditShow = false;
            self.$refs['temp'].resetFields();
          });
        } else {
          return false;
        }
      });
    },
    handleChangeStatus(row, tip, type_, status_) {
      const self = this;
      tip += '【' + row.name + '】';
      this.$confirm(tip, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const params = {
          id: row.id,
          type: type_,
          status: status_
        };
        changeCustomStatus(params).then(response => {
          if (response.code === 20000) {
            self.$message({ message: '操作成功', type: 'success' });
            self.fetchData();
          } else {
            self.$message({ message: response.data, type: 'error' });
          }
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消操作'
        });
      });
    },
    resetUser() {
      this.user = {
        id: undefined,
        name: '',
        mobile: '',
        password: '',
        custom_id: '',
        role_id: ''
      }
    },
    handleEditUser(row) {
      this.user = {
        id: row.id,
        name: row.name,
        mobile: row.mobile,
        password: '',
        custom_id: row.custom_id,
        role_id: row.role_id
      }
      
      this.dlgUserTitle = '编辑账号 - ' + row.mobile
      this.dlgUserShow = true

      if (this.$refs['user'] != undefined) {
        this.$refs['user'].resetFields()
      }
    },
    handleDeleteUser(row) {
      var self = this;
      this.$confirm('您是否确认删除用户 - ' + row.name + ',' + row.mobile + '？')
        .then(_ => {
          deleteUser({
            id: row.id
          }).then(response => {
            self.$message({ message: '删除成功', type: 'success' })
            self.fetchData()
          });
        })
        .catch(_ => {
        })
    },
    handleAddUser(row) {
      this.dlgUserTitle = '创建账号'
      this.resetUser()

      this.user.custom_id = row.id
      this.dlgUserShow = true
      
      if (this.$refs['user'] != undefined) {
        this.$refs['user'].resetFields()
      }
    },
    handleSaveUser() {
      self = this;
      this.$refs['user'].validate((valid) => {
        if (valid) {
          saveUser(this.user).then(response => {
            self.$message({ message: '保存成功', type: 'success' });
            self.fetchData();
            self.dlgUserShow = false;
          });
        } else {
          return false;
        }
      })
    }
  }
};
</script>
