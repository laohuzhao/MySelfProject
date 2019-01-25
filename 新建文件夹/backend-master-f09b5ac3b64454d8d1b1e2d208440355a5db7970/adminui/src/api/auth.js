import fetch from '@/utils/fetch';

// 权限管理
export function getUserList(params) {
  return fetch({
    url: '/auth/user_list',
    method: 'get',
    params
  });
}

export function saveUser(params) {
  return fetch({
    url: '/auth/user_save',
    method: 'post',
    data: params
  });
}

export function deleteUser(params) {
  return fetch({
    url: '/auth/user_delete',
    method: 'post',
    data: params
  });
}

export function selectRole(params) {
  return fetch({
    url: '/auth/select_role',
    method: 'post',
    data: params
  });
}

export function getRoleList(params) {
  return fetch({
    url: '/auth/role_list',
    method: 'post',
    data: params
  });
}

export function saveRole(params) {
  return fetch({
    url: '/auth/role_save',
    method: 'post',
    data: params
  });
}

export function deleteRole(params) {
  return fetch({
    url: '/auth/role_delete',
    method: 'post',
    data: params
  });
}

export function getAllMenu_SYS() {
  return [{
    id: 'device',
    label: '设备管理',
    children: [{
      id: 'device:map',
      label: '地图'
    }, {
      id: 'device:list',
      label: '查看'
    }, {
      id: 'device:edit',
      label: '编辑'
    }, {
      id: 'device:at',
      label: '指令执行'
    }]
  }, {
    id: 'custom',
    label: '账号管理',
    children: [{
      id: 'custom:list',
      label: '查看'
    }, {
      id: 'custom:edit',
      label: '编辑'
    }]
  }, {
    id: 'sysconfig',
    label: '系统设置',
    sys: true,
    children: [{
      id: 'sysconfig:dtype',
      label: '行业代码表'
    }, {
      id: 'sysconfig:manufacturer',
      label: '生产厂家表'
    }, {
      id: 'sysconfig:areacode',
      label: '地域码表'
    }, {
      id: 'sysconfig:paramset',
      label: '参数设置'
    }]
  }, {
    id: 'auth',
    label: '权限管理',
    children: [
      {
        id: 'auth_role',
        label: '角色管理',
        children: [
          { id: 'auth_role:list', label: '查看' },
          { id: 'auth_role:edit', label: '编辑' }
        ]
      }, {
        id: 'auth_user',
        label: '奥特管理员',
        children: [
          { id: 'auth_user:list', label: '查看' },
          { id: 'auth_user:edit', label: '编辑' }
        ]
      }
    ]
  }, {
    id: 'warn',
    label: '预警管理',
    children: [{
      id: 'warn:list',
      label: '查看'
    }]
  }]
}

export function getAllMenu_CUSTOM() {
  return [{
    id: 'device',
    label: '设备管理',
    children: [{
      id: 'device:map',
      label: '地图'
    }, {
      id: 'device:list',
      label: '查看'
    }, {
      id: 'device:edit',
      label: '编辑'
    }, {
      id: 'device:at',
      label: '指令执行'
    }]
  }, {
    id: 'custom',
    label: '账号管理',
    children: [{
      id: 'custom:list',
      label: '查看'
    }, {
      id: 'custom:edit',
      label: '编辑'
    }]
  }, {
    id: 'warn',
    label: '预警管理',
    sys: true,
    children: [{
      id: 'warn:list',
      label: '查看'
    }]
  }]
}

