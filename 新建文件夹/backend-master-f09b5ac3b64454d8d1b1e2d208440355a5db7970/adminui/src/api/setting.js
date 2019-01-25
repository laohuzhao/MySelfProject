import fetch from '@/utils/fetch';

export function getSetting(params) {
  return fetch({
    url: '/setting/index',
    method: 'post',
    data: params
  });
}

export function saveSetting(params) {
  return fetch({
    url: '/setting/save',
    method: 'post',
    data: params
  });
}

export function getDictList(params) {
  return fetch({
    url: '/dict/list',
    method: 'post',
    data: params
  });
}

export function saveDict(params) {
  return fetch({
    url: '/dict/save',
    method: 'post',
    data: params
  });
}

export function deleteDict(params) {
  return fetch({
    url: '/dict/delete',
    method: 'post',
    data: params
  });
}