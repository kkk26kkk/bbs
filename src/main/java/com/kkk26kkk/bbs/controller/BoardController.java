package com.kkk26kkk.bbs.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.kkk26kkk.bbs.model.Article;
import com.kkk26kkk.bbs.model.ArticleDto;
import com.kkk26kkk.bbs.service.BoardService;
import com.kkk26kkk.common.model.Page;
import com.kkk26kkk.common.model.Path;

@Controller
public class BoardController {
	@Autowired
	private BoardService boardService;
//	@Autowired
//	private Environment environment;

	@RequestMapping(value = "/board/list", method = RequestMethod.GET)
	String showBoard(Model model, @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
	    int pageSize = 10; // �� ȭ�鿡 ����� ���ڵ� ����

	    int totalCount = boardService.getArticleCount(); // �� ����Ʈ ���� �޾ƿɴϴ�.

	    // �� ������ ��
	    int totalPage = (totalCount + pageSize - 1) / pageSize;

	    // ���� �������� ������ ���� ������ ��(1, 11, 21, ...)
	    int startPage = ((page - 1) / 10) * 10 + 1;

	    // ���� �������� ������ ������ ������ ��(10, 20, 30, ...)
	    int endPage = startPage + 10 - 1;

	    if (endPage > totalPage)
	      endPage = totalPage;
	    
	    Page p = new Page();
	    p.setStartNum((page - 1) * 10 + 1);
	    p.setEndNum(page * 10);
		
		List<Article> articleList = boardService.getArticleList(p);
//		int length = 0;
//		for(Article board : boardList) {
//			if(null == board.getContents()) {
//				continue;
//			}
//			board.showContents();
//			if(10 == ++length) {
//				break;
//			}
//		}
		List<ArticleDto> boardContents = articleList.stream()
				.map(Article::showContents)
//				.filter(v -> null == v.getContents())
//				.limit(10)
				.collect(Collectors.toList());
		
		model.addAttribute("page", page);
		model.addAttribute("maxPage", totalPage);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("boardContents", boardContents);
		model.addAttribute("writeFormLink", Path.WriteForm.getPath());
		
		return "/board/articleList";
	}
	
}
