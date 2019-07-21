package com.kkk26kkk.bbs.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.kkk26kkk.bbs.model.BoardVO;
import com.kkk26kkk.bbs.model.UserVO;
import com.kkk26kkk.bbs.service.BoardService;
import com.kkk26kkk.bbs.service.UserService;

@Controller
public class BoardController {
	@Autowired
	private BoardService boardService;
	@Autowired
	private UserService userService;

	@RequestMapping(value = "/board_list", method = RequestMethod.GET)
	String getBoardList(Model model, @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
	    int limit = 10; // �� ȭ�鿡 ����� ���ڵ� ����

	    int listCount = boardService.getBoardListCount(); // �� ����Ʈ ���� �޾ƿɴϴ�.

	    // �� ������ ��
	    int maxPage = (listCount + limit - 1) / limit;

	    // ���� �������� ������ ���� ������ ��(1, 11, 21, ...)
	    int startPage = ((page - 1) / 10) * 10 + 1;

	    // ���� �������� ������ ������ ������ ��(10, 20, 30, ...)
	    int endPage = startPage + 10 - 1;

	    if (endPage > maxPage)
	      endPage = maxPage;
		
		List<BoardVO> boardList = boardService.getBoardList(page);
		
		model.addAttribute("page", page);
		model.addAttribute("maxpage", maxPage);
		model.addAttribute("startpage", startPage);
		model.addAttribute("endpage", endPage);
		model.addAttribute("listcount", listCount);
		model.addAttribute("boardList", boardList);
		
		return "/board/board_list";
	}
	
	@RequestMapping(value = "/board_view", method = RequestMethod.GET)
	String getBoard(@RequestParam("idx") int idx, @RequestParam("page") int page,
			Model model) {
		BoardVO board = boardService.getBoard(idx);
		
		model.addAttribute("board", board);
		model.addAttribute("page", page);
		
		return "/board/board_view";
	}

	@RequestMapping(value = "/board_delete") 
	String boardDelete(@RequestParam int idx, Model model) {
		boardService.deleteBoard(idx);
		
		return "redirect:/board_list";
	}
	
	@RequestMapping(value = "/board_edit") 
	String boardEdit(@RequestParam int idx, @RequestParam("page") int page,
			Model model) {
		BoardVO board = boardService.getBoard(idx);
		
		model.addAttribute("board", board);
		
		return "/board/board_edit";
	}
	
	@RequestMapping(value = "/board_edit_ok", method = RequestMethod.POST)
	String boardEditOk(BoardVO board) {
		boardService.updateBoard(board);
		
		return "redirect:/board_list";
	}
	
	@RequestMapping(value = "/board_write")
	String boardWrite() {
		return "/board/board_write";
	}
	
	@RequestMapping(value = "/board_write_ok")
	String boardWriteOk(BoardVO board, HttpServletRequest request) {
		UserVO user = userService.getUserInfo(request.getSession().getAttribute("userId").toString());
		
		board.setUserId(user.getId());
		board.setUserName(user.getName());
		board.setPw(user.getPw());
		boardService.insertBoard(board);
		
		return "redirect:/board_list";
	}
	
	@RequestMapping(value = "/board_reply")
	String boardReply(@RequestParam int idx, @RequestParam("page") int page,
			Model model) {
		BoardVO board = boardService.getBoard(idx);
		
		model.addAttribute("board", board);
		model.addAttribute("page", page);
		
		return "/board/board_reply";
	}
	
	@RequestMapping(value = "/board_reply_ok")
	String boardReplyOk(BoardVO board, @RequestParam("page") int page,
			HttpServletRequest request) {
		UserVO user = userService.getUserInfo(request.getSession().getAttribute("userId").toString());
		
		board.setUserId(user.getId());
		board.setUserName(user.getName());
		board.setPw(user.getPw());
		board.setGroupNo(board.getGroupNo());
		board.setGroupOrder(board.getGroupOrder() + 1);
		board.setGroupLayer(board.getGroupLayer() + 1);
		
		boardService.replyBoard(board);
		
		return "redirect:/board_list?page=" + page;
	}
}
