<template>
  <div class="app-container calendar-list-container">
    <el-tabs v-model="activeName">
      <el-tab-pane label="奥特角色" name="tab1">
        <div class="filter-container" style="padding-bottom: 15px;">
          <el-button class="filter-item" type="primary" icon="plus" :disabled="!auth.edit" @click="handleNew('SYS')">新增奥特角色</el-button>
        </div>
        <el-table :data="listSys" v-loading.body="listSysLoading" element-loading-text="拼命加载中" border fit highlight-current-row style="width: 100%">
          <el-table-column label="角色" width="200">
            <template scope="scope">{{scope.row.name}}</template>
          </el-table-column>
          <el-table-column label="权限">
            <template scope="scope">{{scope.row.auth_list_}}</template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template scope="scope">
              <el-button type="text" size="small" :disabled="!auth.edit" @click="handleEdit('SYS', scope.row)">编辑</el-button>
              <el-button v-if="scope.row.id != 'SYS'" type="text" size="small" :disabled="!auth.edit" @click="handleDelete('SYS', scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="客户角色" name="tab2">
        <div class="filter-container" style="padding-bottom: 15px;">
          <el-button class="filter-item" type="primary" icon="plus" :disabled="!auth.edit" @click="handleNew('CUSTOM')">新增客户角色</el-button>
        </div>
        <el-table :data="listCustom" v-loading.body="listCustomLoading" element-loading-text="拼命加载中" border fit highlight-current-row style="width: 100%">
          <el-table-column label="角色" width="200">
            <template scope="scope">{{scope.row.name}}</template>
          </el-table-column>
          <el-table-column label="权限">
            <template scope="scope">{{scope.row.auth_list_}}</template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template scope="scope">
              <el-button type="text" size="small" :disabled="!auth.edit" @click="handleEdit('CUSTOM', scope.row)">编辑</el-button>
              <el-button v-if="scope.row.id != 'CUSTOM'" type="text" size="small" :disabled="!auth.edit" @click="handleDelete('CUSTOM', scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <el-dialog :title="dlgRoleTitle" :visible.sync="dlgRoleShow" style="height:500px;" :close-on-click-modal="false">
      <el-form class="small-space" :model="temp" ref="temp" :rules="rules" label-position="left" label-width="80px" style='margin-left:50px;'>
        <el-form-item label="角色名称" prop="name">
          <el-input v-if="temp.id != 'SYS' && temp.id != 'CUSTOM'" v-model="temp.name" style="width:100%"></el-input>
          <span v-else>{{temp.name}}</span>
        </el-form-item>
        <el-form-item label="权限分配">
          <el-tree :data="allAuth"  style="width:100%" show-checkbox node-key="id" ref="auth_tree" highlight-current :props="defaultProps">
          </el-tree>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleCancel">取 消</el-button>
        <el-button type="primary" @click="handleSave">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { mapGetters } from 'vuex';
import { getRoleList, saveRole, deleteRole, getAllMenu_SYS, getAllMenu_CUSTOM } from '@/api/auth';
  
export default {
  computed: {
    ...mapGetters([
      'name',
      'roles',
      'user_type'
    ])
  },
  data() {
    return {
      activeName: 'tab1',
      listSysLoading: true,
      listSys: null,
      listCustomLoading: true,
      listCustom: null,
      dlgRoleShow: false,
      dlgRoleTitle: '',
      temp: {
        id: undefined,
        type: '',
        name: '',
        auth_list: {}
      },
      rules: {
        name: [
          { required: true, message: '请输入角色名称', trigger: 'blur' }
        ]
      },
      allAuth: [],
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      // 权限
      auth: {
        edit: true
      }
    }
  },
  created() {
    this.auth.edit = this.roles.indexOf('auth_role:edit') > -1
    this.fetchDataSYS();
    this.fetchDataCUSTOM();
    
    this.allAuth = getAllMenu_SYS()
    var _tip = {};
    for (var key in this.allAuth) {
      _tip[this.allAuth[key].id] = this.allAuth[key].label;
      var chld = this.allAuth[key].children;
      for (var k1 in chld) {
        _tip[chld[k1].id] = chld[k1].label;
        if (chld[k1].children !== undefined) {
          for (var k2 in chld[k1].children) {
            _tip[chld[k1].children[k2].id] = chld[k1].children[k2].label;
          }
        }
      }
    }
    this.auth_tip = _tip;
  },
  methods: {
    fetchDataSYS() {
      this.listSysLoading = true;
      getRoleList({type: 'SYS'}).then(response => {
        var items = response.data.items.map(v => {
          var auth_list_ = ''
          if (v.auth_list !== undefined && v.auth_list.length > 0) {
            var arr = JSON.parse(v.auth_list);
            for (var j = 0; j < arr.length; j++) {
              var tip_ = this.auth_tip[arr[j]];
              if (tip_ !== undefined) {
                var n = arr[j].indexOf(':');
                if (n > 0) {
                  var pre = arr[j].substring(0, n);
                  if (v.auth_list.indexOf('"' + pre + '"') > 0) {
                    auth_list_ += tip_ + '；';
                  } else {
                    auth_list_ += '【' + this.auth_tip[pre] + '】' + tip_ + '；';
                  }
                } else {
                  auth_list_ += '【' + tip_ + '】';
                }
              }
            }
          }
          v.auth_list_ = auth_list_
          return v
        })

        this.listSys = items;
        this.listSysLoading = false;
      })
    },
    fetchDataCUSTOM() {
      this.listCustomLoading = true;
      getRoleList({type: 'CUSTOM'}).then(response => {
        var items = response.data.items.map(v => {
          var auth_list_ = ''
          if (v.auth_list !== undefined && v.auth_list.length > 0) {
            var arr = JSON.parse(v.auth_list);
            for (var j = 0; j < arr.length; j++) {
              var tip_ = this.auth_tip[arr[j]];
              if (tip_ !== undefined) {
                var n = arr[j].indexOf(':');
                if (n > 0) {
                  var pre = arr[j].substring(0, n);
                  if (v.auth_list.indexOf('"' + pre + '"') > 0) {
                    auth_list_ += tip_ + '；';
                  } else {
                    auth_list_ += '【' + this.auth_tip[pre] + '】' + tip_ + '；';
                  }
                } else {
                  auth_list_ += '【' + tip_ + '】';
                }
              }
            }
          }
          v.auth_list_ = auth_list_
          return v
        })

        this.listCustom = items;
        this.listCustomLoading = false;
      })
    },
    resetTemp() {
      this.temp = {
        id: undefined,
        type: '',
        name: '',
        auth_list: {}
      };
    },
    handleNew(type) {
      this.resetTemp();
      
      if (type == 'SYS') {
        this.dlgRoleTitle = '创建奥特角色'
        this.allAuth = getAllMenu_SYS()
      } else {
        this.dlgRoleTitle = '创建客户角色'
        this.allAuth = getAllMenu_CUSTOM()
      }
      this.temp.type = type
      this.dlgRoleShow = true;
    },
    handleEdit(type, row) {
      this.temp.id = row.id
      this.temp.type = row.type
      this.temp.name = row.name

      if (type == 'SYS') {
        this.dlgRoleTitle = '编辑奥特角色'
        this.allAuth = getAllMenu_SYS()
      } else {
        this.dlgRoleTitle = '编辑客户角色'
        this.allAuth = getAllMenu_CUSTOM()
      }
      this.dlgRoleShow = true
      setTimeout(() => {
        var checked = [];
        if (row.auth_list != undefined && row.auth_list.indexOf('[') == 0) {
          var arr = JSON.parse(row.auth_list);
          for (var i = 0; i < arr.length; i++) {
            checked.push(arr[i]);
          }
        }
        if (this.$refs.auth_tree != undefined) {
          this.$refs.auth_tree.setCheckedKeys(checked);
        }
      }, 500);
    },
    handleDelete(index, row) {
      self = this;
      this.$confirm('您是否确认删除角色 - ' + row.name + '？')
        .then(_ => {
          deleteRole({
            id: row.id
          }).then(response => {
            self.$message({ message: response.data, type: 'success' });
            if (row.type == 'SYS') {
              self.fetchDataSYS()
            } else {
              self.fetchDataCUSTOM()
            }
          });
        })
        .catch(_ => {
        });
    },
    handleCancel() {
      this.dlgRoleShow = false;
      this.$refs['temp'].resetFields();
      this.$refs.auth_tree.setCheckedKeys([]);
    },
    handleSave() {
      self = this;
      const treekey = JSON.stringify(this.$refs.auth_tree.getCheckedKeys());
      this.$refs['temp'].validate((valid) => {
        if (valid) {
          saveRole({
            id: self.temp.id,
            type: self.temp.type,
            name: self.temp.name,
            auth_list: treekey
          }).then(response => {
            self.$message({ message: response.data, type: 'info' });
            if (self.temp.type == 'SYS') {
              self.fetchDataSYS()
            } else {
              self.fetchDataCUSTOM()
            }
            self.dlgRoleShow = false;
            self.$refs['temp'].resetFields();
            self.$refs.auth_tree.setCheckedKeys([]);
          });
        } else {
          return false;
        }
      });
    }
  }
};
</script>
