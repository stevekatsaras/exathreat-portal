package com.exathreat.common.support;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.expression.ThymeleafEvaluationContext;

@Component
public class MailSupport {

	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private TemplateEngine templateEngine;
	
	public void sendEmail(String to, String subject, String template, Map<String, Object> variables) throws Exception {
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
			messageHelper.setTo(to);
			messageHelper.setSubject(subject);
			messageHelper.setText(build(template, variables), true);
			messageHelper.addInline("logo", new ClassPathResource("static/img/logo.png"));
		};
		javaMailSender.send(messagePreparator);
	}

	public void sendEmail(String[] to, String subject, String template, Map<String, Object> variables) throws Exception {
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
			messageHelper.setTo(to);
			messageHelper.setSubject(subject);
			messageHelper.setText(build(template, variables), true);
			messageHelper.addInline("logo", new ClassPathResource("static/img/logo.png"));
		};
		javaMailSender.send(messagePreparator);
	}

	private String build(String template, Map<String, Object> variables) {
		Context context = new Context();
		context.setVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, new ThymeleafEvaluationContext(applicationContext, null));
		context.setVariables(variables);
		return templateEngine.process(template, context);
	}
}