import fetch from '@/utils/fetch';

export function dashboard(params) {
  return fetch({
    url: '/device/dashboard',
    method: 'post',
    data: params
  });
}

export function getDeviceList(params) {
  return fetch({
    url: '/device/list',
    method: 'post',
    data: params
  });
}

export function getDeviceMap(params) {
  return fetch({
    url: '/device/map',
    method: 'post',
    data: params
  });
}

export function saveDevice(params) {
  return fetch({
    url: '/device/save',
    method: 'post',
    data: params
  });
}

export function saveParams(params) {
  return fetch({
    url: '/device/set_params',
    method: 'post',
    data: params
  });
}

export function setId(params) {
  return fetch({
    url: '/device/setid',
    method: 'post',
    data: params
  });
}

export function saveAlloc(params) {
  return fetch({
    url: '/device/alloc',
    method: 'post',
    data: params
  });
}

export function initDevice(params) {
  return fetch({
    url: '/device/init',
    method: 'post',
    data: params
  });
}

export function atCmd(params) {
  return fetch({
    url: '/device/at',
    method: 'post',
    data: params
  });
}

export function searchCustom(params) {
  return fetch({
    url: '/device/select_custom',
    method: 'post',
    data: params
  });
}

export function getSetting(params) {
  return fetch({
    url: '/device/setting',
    method: 'post',
    data: params
  });
}

export function saveSetting(params) {
  return fetch({
    url: '/device/setting_save',
    method: 'post',
    data: params
  });
}
