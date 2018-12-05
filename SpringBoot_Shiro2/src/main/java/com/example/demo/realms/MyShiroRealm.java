package com.example.demo.realms;

import java.util.HashSet;
import java.util.Set;

import com.example.demo.entity.User;
import com.example.demo.entity.permission;
import com.example.demo.entity.role;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * realm实现类,用于实现具体的验证和授权方法
 * @author Bean
 *
 */
public class MyShiroRealm extends AuthorizingRealm {
	/*//用于用户查询
	@Autowired
	private UserService loginService;*/

	/**
	 * 方面用于加密 参数：AuthenticationToken是从表单穿过来封装好的对象
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
		System.out.println("开始进行身份验证");
		//加这一步的目的是在Post请求的时候会先进认证，然后在到请求
		if (authenticationToken.getPrincipal() == null) {
			System.out.println("没有输入用户名");
			return null;
		}
		//获取用户信息
		String name = authenticationToken.getPrincipal().toString();
		//开始模拟数据库查找到数据
		User user=new User();
		user.setId(1);
		user.setUsername("test");
		user.setPassword("fc1709d0a95a6be30bc5926fdb7f22f4");
		//模拟结束

		/*User user = loginService.findByName(name);根据用户名查找信息，如果有说明用户正确*/
		if (!name.equals(user.getUsername())) {//这里验证用户名
			//这里返回后会报出对应异常
			System.out.println("用户名有问题");
			return null;
		} else {
			//这里验证authenticationToken和simpleAuthenticationInfo的信息，这里验证密码
			SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(name, user.getPassword(), getName());
			return simpleAuthenticationInfo;
		}
	}





		/*System.out.println("doGetAuthenticationInfo:" + token);

		// 将AuthenticationToken强转为AuthenticationToken对象
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;

		// 获得从表单传过来的用户名
		String username = upToken.getUsername();

		// 从数据库查看是否存在用户
		UserService userService = new UserService();

		// 如果用户不存在，抛此异常
		if (!userService.selectUsername(username)) {
			throw new UnknownAccountException("无此用户名！");
		}

		// 认证的实体信息，可以是username，也可以是用户的实体类对象，这里用的用户名
		Object principal = username;
		// 从数据库中查询的密码
		Object credentials = userService.selectPassword(username);
		// 颜值加密的颜，可以用用户名
		ByteSource credentialsSalt = ByteSource.Util.bytes(username);
		// 当前realm对象的名称，调用分类的getName()
		String realmName = this.getName();

		// 创建SimpleAuthenticationInfo对象，并且把username和password等信息封装到里面
		// 用户密码的比对是Shiro帮我们完成的
		SimpleAuthenticationInfo info = null;
		info = new SimpleAuthenticationInfo(principal, credentials, credentialsSalt, realmName);

		return info;*/


	// 用于授权
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

		//获取登录用户名
		String name= (String) principals.getPrimaryPrincipal();

		//开始模拟数据库查找到数据
		User user=new User();
		user.setId(1);
		user.setUsername("test");
		user.setPassword("fc1709d0a95a6be30bc5926fdb7f22f4");//123456加密后
		//模拟结束

		/*//查询用户名称
		User user = loginService.findByName(name);*/
		//添加角色和权限

		SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
		//开始模拟角色
		role r=new role();
		r.setUserId(1);
		r.setId(1);
		r.setRole("admin");
		//开始模拟权限
		permission p=new permission();
		p.setId(1);
		p.setLimit("a");
		p.setRoleId(1);
		//模拟结束


		//添加角色,后边用来限制访问
		simpleAuthorizationInfo.addRole(r.getRole());

		//添加权限，后边用来限制访问
		simpleAuthorizationInfo.addStringPermission(p.getLimit());
		return simpleAuthorizationInfo;


	/*	System.out.println("MyShiroRealm的doGetAuthorizationInfo授权方法执行");

		// User user=(User)
		// principals.fromRealm(this.getClass().getName()).iterator().next();//获取session中的用户
		// System.out.println("在MyShiroRealm中AuthorizationInfo（授权）方法中从session中获取的user对象:"+user);

		// 从PrincipalCollection中获得用户信息
		Object principal = principals.getPrimaryPrincipal();
		System.out.println("ShiroRealm  AuthorizationInfo:" + principal.toString());

		// 根据用户名来查询数据库赋予用户角色,权限（查数据库）
		Set<String> roles = new HashSet<>();//角色对象
		Set<String> permissions = new HashSet<>();//权限对象

		roles.add("user");

		permissions.add("user:query");

		if ("admin".equals(principal)) {
			roles.add("admin");
			permissions.add("admin:query");
		}

		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roles);
		//添加权限
		info.setStringPermissions(permissions);
		return info;
		// return null;*/
	}

}