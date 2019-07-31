package com.kkk26kkk.bbs.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kkk26kkk.bbs.model.User;
import com.kkk26kkk.bbs.model.UserDto;
import com.kkk26kkk.bbs.service.UserService;

@Controller
public class UserController {
	@Autowired
	private UserService userService;
	
	@RequestMapping("/login")
	String login() {
		return "/user/login";
	}
	
	@RequestMapping("/logout")
	String logout(HttpServletRequest request) {
		request.getSession().invalidate();
		
		return "redirect:/board/list";
	}
	
	@RequestMapping(value = "/login_ok", method = RequestMethod.POST, headers = "Content-Type=application/json")
	@ResponseBody
	Map<String, Object> loginOk(@RequestBody UserDto loginUser, HttpServletRequest request) {
		Map<String, Object> result = new HashMap<>();
		
		User getUser = userService.getUser(loginUser.getUserId());
		
		try {
			if(loginUser.getUserPw().equals(getUser.getUserPw())) { // user.getHashPw() �� ����
				request.getSession().setAttribute("userId", getUser.getUserId());
				request.getSession().setAttribute("userName", getUser.getUserName());
				result.put("code", HttpStatus.OK);
				result.put("redirect", request.getContextPath() + "/board/list");
			} else {
				result.put("msg", "�ش� ������ �������� �ʽ��ϴ�.");
	            result.put("code", HttpStatus.NOT_FOUND);
			}
		} catch(Exception e) {
			result.put("msg", "�ش� ������ �������� �ʽ��ϴ�.");
            result.put("code", HttpStatus.NOT_FOUND);
			e.printStackTrace();
		}
		
		return result;
	}
	
}
