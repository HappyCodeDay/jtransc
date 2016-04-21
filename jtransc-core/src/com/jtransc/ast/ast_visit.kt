package com.jtransc.ast

open class AstVisitor {
	open fun visit(body: AstBody) {
		visit(body.stm)
	}

	open fun visitStms(stms: List<AstStm?>?) {
		if (stms != null) for (e in stms) visit(e)
	}

	open fun visitExprs(exprs: List<AstExpr?>?) {
		if (exprs != null) for (e in exprs) visit(e)
	}

	open fun visit(stm: AstStm?) {
		when (stm) {
			null -> Unit
			is AstStm.STMS -> visit(stm)
			is AstStm.NOP -> visit(stm)
			is AstStm.STM_EXPR -> visit(stm)
			is AstStm.SET -> visit(stm)
			is AstStm.SET_ARRAY -> visit(stm)
			is AstStm.SET_FIELD_STATIC -> visit(stm)
			is AstStm.SET_FIELD_INSTANCE -> visit(stm)
			is AstStm.SET_NEW_WITH_CONSTRUCTOR -> visit(stm)
			is AstStm.IF -> visit(stm)
			is AstStm.WHILE -> visit(stm)
			is AstStm.RETURN -> visit(stm)
			is AstStm.THROW -> visit(stm)
			is AstStm.RETHROW  -> visit(stm)
			is AstStm.TRY_CATCH -> visit(stm)
			is AstStm.BREAK -> visit(stm)
			is AstStm.CONTINUE -> visit(stm)
			is AstStm.SWITCH -> visit(stm)
			is AstStm.STM_LABEL -> visit(stm)
			is AstStm.IF_GOTO -> visit(stm)
			is AstStm.SWITCH_GOTO -> visit(stm)
			is AstStm.MONITOR_ENTER -> visit(stm)
			is AstStm.MONITOR_EXIT -> visit(stm)
		}
	}

	open fun visit(expr: AstExpr?) {
		when (expr) {
			null -> Unit
			is AstExpr.THIS -> visit(expr)
			is AstExpr.LITERAL -> visit(expr)
			is AstExpr.LOCAL -> visit(expr)
			is AstExpr.PARAM -> visit(expr)
			is AstExpr.CAUGHT_EXCEPTION -> visit(expr)
			is AstExpr.BINOP -> visit(expr)
			is AstExpr.UNOP -> visit(expr)
			is AstExpr.CALL_BASE -> visit(expr)
			is AstExpr.ARRAY_LENGTH -> visit(expr)
			is AstExpr.ARRAY_ACCESS -> visit(expr)
			is AstExpr.INSTANCE_FIELD_ACCESS -> visit(expr)
			is AstExpr.STATIC_FIELD_ACCESS -> visit(expr)
			is AstExpr.INSTANCE_OF -> visit(expr)
			is AstExpr.CAST -> visit(expr)
			is AstExpr.NEW -> visit(expr)
			is AstExpr.NEW_WITH_CONSTRUCTOR -> visit(expr)
			is AstExpr.NEW_ARRAY -> visit(expr)
		}
	}

	open fun visit(expr: AstExpr.CALL_BASE) {
		visit(expr.method)
		for (arg in expr.args) visit(arg)

		when (expr) {
			is AstExpr.CALL_INSTANCE -> visit(expr)
			is AstExpr.CALL_SUPER -> visit(expr)
			is AstExpr.CALL_STATIC -> visit(expr)
		}
	}

	open fun visit(ref: AstType.REF) {
	}

	open fun visit(local: AstLocal) {
	}

	open fun visit(label: AstLabel) {
	}

	open fun visit(ref: AstType) {
		for (c in ref.getRefClasses()) visit(c)
	}

	open fun visit(ref: FqName) {
		visit(AstType.REF(ref))
	}

	open fun visit(ref: AstFieldRef) {
		//visit(AstType.REF(ref))
	}

	open fun visit(ref: AstMethodRef) {
	}

	open fun visit(argument: AstArgument) {
	}

	open fun visit(stm: AstStm.STMS) {
		visitStms(stm.stms)
	}

	open fun visit(stm: AstStm.NOP) {
	}

	open fun visit(stm: AstStm.SET) {
		visit(stm.local)
		visit(stm.expr)
	}

	open fun visit(stm: AstStm.SET_ARRAY) {
		visit(stm.array)
		visit(stm.index)
		visit(stm.expr)
	}

	open fun visit(stm: AstStm.SET_FIELD_STATIC) {
		visit(stm.clazz)
		visit(stm.field)
		visit(stm.expr)
	}

	open fun visit(stm: AstStm.SET_FIELD_INSTANCE) {
		visit(stm.left)
		visit(stm.field)
		visit(stm.expr)
	}

	open fun visit(stm: AstStm.IF) {
		visit(stm.cond)
		visit(stm.strue)
		visit(stm.sfalse)
	}

	open fun visit(stm: AstStm.WHILE) {
		visit(stm.cond)
		visit(stm.iter)
	}

	open fun visit(stm: AstStm.RETURN) {
		visit(stm.retval)
	}

	open fun visit(stm: AstStm.THROW) {
		visit(stm.value)
	}

	open fun visit(stm: AstStm.RETHROW) {
	}

	open fun visit(stm: AstStm.TRY_CATCH) {
		visit(stm.trystm)
		visit(stm.catch)
	}

	open fun visit(stm: AstStm.SWITCH) {
		visit(stm.subject)
		visit(stm.default)
		for ((value, case) in stm.cases) {
			visit(case)
		}
	}

	open fun visit(stm: AstStm.STM_LABEL) {
		visit(stm.label)
	}

	open fun visit(stm: AstStm.IF_GOTO) {
		visit(stm.label)
		visit(stm.cond)
	}

	open fun visit(stm: AstStm.SWITCH_GOTO) {
		visit(stm.subject)
		visit(stm.default)
		for ((value, case) in stm.cases) {
			visit(case)
		}
	}

	open fun visit(stm: AstStm.MONITOR_ENTER) {
		visit(stm.expr)
	}

	open fun visit(stm: AstStm.MONITOR_EXIT) {
		visit(stm.expr)
	}

	open fun visit(stm: AstStm.SET_NEW_WITH_CONSTRUCTOR) {
		visit(stm.local)
		visit(stm.method)
		visit(stm.target)
		visitExprs(stm.args)
	}

	open fun visit(stm: AstStm.BREAK) {
	}

	open fun visit(stm: AstStm.CONTINUE) {
	}

	open fun visit(stm: AstStm.STM_EXPR) {
		visit(stm.expr)
	}

	open fun visit(expr: AstExpr.THIS) {
		visit(expr.ref)
	}

	open fun visit(expr: AstExpr.LITERAL) {
		if (expr.value is AstType) {
			visit(expr.value)
		}
	}

	open fun visit(expr: AstExpr.LOCAL) {
		visit(expr.local)
	}

	open fun visit(expr: AstExpr.PARAM) {
		visit(expr.argument)
	}

	open fun visit(expr: AstExpr.CAUGHT_EXCEPTION) {
		visit(expr.type)
	}

	open fun visit(expr: AstExpr.BINOP) {
		visit(expr.left)
		visit(expr.right)
	}

	open fun visit(expr: AstExpr.UNOP) {
		visit(expr.right)
	}


	open fun visit(expr: AstExpr.CALL_INSTANCE) {
		visit(expr.obj)
	}

	open fun visit(expr: AstExpr.CALL_SUPER) {
		visit(expr.obj)

	}

	open fun visit(expr: AstExpr.CALL_STATIC) {
	}

	open fun visit(expr: AstExpr.ARRAY_LENGTH) {
		visit(expr.array)

	}
	open fun visit(expr: AstExpr.ARRAY_ACCESS) {
		visit(expr.array)
		visit(expr.index)
	}

	open fun visit(expr: AstExpr.INSTANCE_FIELD_ACCESS) {
		visit(expr.field)
		visit(expr.expr)
	}

	open fun visit(expr: AstExpr.STATIC_FIELD_ACCESS) {
		visit(expr.field)
	}

	open fun visit(expr: AstExpr.INSTANCE_OF) {
		visit(expr.expr)
		visit(expr.checkType)
	}

	open fun visit(expr: AstExpr.CAST) {
		visit(expr.expr)
		visit(expr.type)
	}

	open fun visit(expr: AstExpr.NEW) {
		visit(expr.target)
		visit(expr.type)
	}

	open fun visit(expr: AstExpr.NEW_WITH_CONSTRUCTOR) {
		visit(expr.target)
		visit(expr.type)
		visitExprs(expr.args)
	}

	open fun visit(expr: AstExpr.NEW_ARRAY) {
		visit(expr.arrayType)
		visitExprs(expr.counts)
	}
}
