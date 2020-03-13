package com.efimchik.ifmo.web.mvc.controller;

import com.efimchik.ifmo.web.mvc.status.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/calc/")
public class ExpressionController {
    private final static String SESSION_EXPRESSION = "expression";
    private final static String SESSION_VARIABLE = "variable_";
    private final static int LOWER_BOUND = -10000;
    private final static int HIGHER_BOUND = 10000;

    private int responseCode = 400;

    @PutMapping("equation")
    @ResponseBody
    public ResponseEntity<String> putExpression(@RequestBody String expression, HttpSession session) {
        setExpressionSession(session, expression);
        return new ResponseEntity<>("", HttpStatus.valueOf(responseCode));
    }

    @PutMapping("{var}")
    @ResponseBody
    public ResponseEntity<String> putVariable(@PathVariable String var, @RequestBody String value, HttpSession session) {
        setVariableSession(session, var, value);
        return new ResponseEntity<>("", HttpStatus.valueOf(responseCode));
    }

    @DeleteMapping("equation")
    @ResponseBody
    public ResponseEntity<String> deleteExpression(HttpSession session) {
        deleteSession(session, SESSION_EXPRESSION);
        return new ResponseEntity<>("", HttpStatus.valueOf(responseCode));
    }

    @DeleteMapping("{var}")
    @ResponseBody
    public ResponseEntity<String> deleteVariable(@PathVariable String var, HttpSession session) {
        deleteSession(session, (SESSION_VARIABLE + var));
        return new ResponseEntity<>("", HttpStatus.valueOf(responseCode));
    }

    private void setExpressionSession(HttpSession session, String expression) {
        if (!validateExpression(expression)) {
            return;
        }
        if (session.getAttribute(SESSION_EXPRESSION) != null) {
            responseCode = StatusCode.UPDATED.getCode();
        } else {
            responseCode = StatusCode.CREATED.getCode();
        }
        session.setAttribute(SESSION_EXPRESSION, expression);
    }

    private void setVariableSession(HttpSession session, String variable, String value) {
        if (!validateValue(session, value)) {
            return;
        }
        if (session.getAttribute(SESSION_VARIABLE + variable) != null) {
            responseCode = StatusCode.UPDATED.getCode();
        } else {
            responseCode = StatusCode.CREATED.getCode();
        }
        session.setAttribute((SESSION_VARIABLE + variable), value);
    }

    private void deleteSession(HttpSession session, String attributeName) {
        if (session.getAttribute(attributeName) != null) {
            responseCode = StatusCode.DELETED.getCode();
            session.removeAttribute(attributeName);
            return;
        }
        responseCode = StatusCode.INCORRECT.getCode();
    }

    private boolean validateExpression(String expression) {
        if (expression.matches("[^*/+-]+")) {
            responseCode = StatusCode.INCORRECT.getCode();
            return false;
        }
        return true;
    }

    private boolean validateValue(HttpSession session, String value) {
        if (value.matches("[a-z]")) {
            String otherValue = getValue(session, value);
            if (!otherValue.isEmpty()) {
                return validateValue(session, otherValue);
            }
        }
        if (value.matches("[-0-9]+")) {
            int n = Integer.parseInt(value);
            boolean inInterval = n >= LOWER_BOUND && n <= HIGHER_BOUND;
            if (!inInterval) {
                responseCode = StatusCode.EXCEEDED.getCode();
            }
            return inInterval;
        }
        responseCode = StatusCode.INCORRECT.getCode();
        return false;
    }

    private String getValue(HttpSession session, String variable) {
        if (session.getAttribute(SESSION_VARIABLE + variable) != null) {
            return (String) session.getAttribute(SESSION_VARIABLE + variable);
        }
        return "";
    }
}
