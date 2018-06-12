package com.liwc.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.liwc.model.Attachment;
import com.liwc.service.AttachmentService;
import com.liwc.service.FileUploadService;
import com.liwc.util.DateUtil;

@Service
public class FileUploadServiceImpl implements FileUploadService {

	private static final Log logger = LogFactory.getLog(FileUploadServiceImpl.class);

	@Autowired
	private AttachmentService as;

	@Override
	public Map<String, Object> upload(MultipartFile[] files, String basePath, String relaPath) {

		Map<String, Object> map = new HashMap<String, Object>();

		if (files.length <= 0) {
			map.put("msg", "没有要上传的文件！");
			return null;
		}

		String dateTime = DateUtil.getDateTime();
		String midPath = "/upload/" + dateTime + "/";

		Attachment attachment = null;
		for (int i = 0; i < files.length; i++) {

			MultipartFile myfiles = files[i];
			if (myfiles.isEmpty()) {
				logger.warn("no file to be uploaded");
			}

			// 文件原名称
			String orgnName = myfiles.getOriginalFilename();
			File localFile = new File(basePath + midPath + orgnName);
			if (!localFile.exists()) {
				localFile.mkdirs();
			}

			try {
				myfiles.transferTo(localFile);
				map.put("msg", "上传成功！");
				map.put("realPath", basePath + midPath + orgnName);
				map.put("url", midPath + orgnName);
				map.put("full_url", relaPath + midPath + orgnName);

				attachment = new Attachment();
				attachment.setFileName(orgnName);
				attachment.setFilePath(midPath + orgnName);

				as.save(attachment);

			} catch (IllegalStateException e) {
				map.put("msg", "上传失败！");
				logger.error("-->文件上传失败！！！");
				e.printStackTrace();
			} catch (IOException e) {
				map.put("msg", "上传失败！");
				logger.error("-->文件上传失败！！！");
				e.printStackTrace();
			}
		}
		return map;
	}

	@Override
	public ResponseEntity<byte[]> download(String fileId, String basePath, String baseURL) throws IOException {

		// 获取文件信息
		Attachment at = as.byId(fileId);

		String midPath = at.getFilePath();
		// File f = new File(baseURL + midPath);
		String fileOrgnName = at.getFileName();

		/* 优雅的处理方式 https://my.oschina.net/songxinqiang/blog/898901 */

		URL url = new URL(baseURL + midPath);

		File file = null;
		try {
			URI uri = url.toURI();

			// URL 方式
			// file = new File(uri);

			// 物理地址方式
			file = new File(basePath + midPath);

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		HttpHeaders headers = new HttpHeaders();
		// 下载显示的文件名，解决中文名称乱码问题
		String downloadFielName = new String(fileOrgnName.getBytes("UTF-8"), "iso-8859-1");
		headers.setContentDispositionFormData("attachment", downloadFielName);
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);

	}

	@Override
	public Map<String, Object> uploadFdfs(MultipartFile file) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<byte[]> downloadFdfs(String fileId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int deleteFdfs(String fileId) {
		// TODO Auto-generated method stub
		return 0;
	}

}
