package ink.zerohua.autoyiban.service;

import ink.zerohua.autoyiban.component.AutoYiBanBean;
import ink.zerohua.autoyiban.entity.User;
import ink.zerohua.autoyiban.repository.UserRepository;
import ink.zerohua.autoyiban.util.Md5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @program: auto-yiban
 * @author: zerohua
 * @create: 2020-10-17 03:47
 **/
@Service
@Slf4j
public class UserService {

	@Resource
	private UserRepository userRepository;

	public String addUser(User user) {
		user.setMed5Val(Md5Util.getMd5Value(user.getPassword()));
		String result = this.verify(user);
		if ("1".equals(result)) {
			user.setMed5Val(Md5Util.getMd5Value(user.getPassword()));
			userRepository.save(user);
		}
		return result;
	}

	public String updateUser(User user) {
		userRepository.save(user);
		return "成功";
	}


	public String verify(User user) {
		if (this.isExist(user.getYibanAccount())) {
			log.warn(user.getYibanAccount() + "  重复提交..");
			//账号已经存在，请勿重复提交
			return "3";
		}
		if (new AutoYiBanBean().login(user)) {
			//成功注册
			log.info(user.getYibanAccount() + "  注册成功..");
			return "1";
		}else {
			//用户名密码错误
			log.info(user.getYibanAccount() + "  账号密码错误..");
			return "2";
		}
	}

	public boolean isExist(String account) {
		User user = new User();
		user.setYibanAccount(account);
		if (getOne(user) == null) {
			return false;
		}
		return true;
	}

	public User getOne(User user) {
		Example<User> condition = Example.of(user);
		//必须是实例
		List<User> users = userRepository.findAll(condition);
 		if (users != null && !users.isEmpty()) {
 			return users.get(0);
		}
		return null;
	}
}
