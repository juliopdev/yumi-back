package com.yumi.audit.infrastructure.audit;

import com.yumi.audit.application.mapper.AdminAuditLogMapper;
import com.yumi.audit.infrastructure.persistence.MongoAdminAuditLogRepository;
import com.yumi.auth.domain.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.Set;

/**
 * Aspecto que intercepta las peticiones a *AdminController
 * y persiste un log de auditoría si el rol está autorizado.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class AdminAuditAspect {

  private static final Set<String> ROLES_TO_LOG = Set.of(
      UserRole.ADMIN.name(),
      UserRole.INVENTORYMANAGER.name(),
      UserRole.SHIPPINGMANAGER.name());

  private final MongoAdminAuditLogRepository repository;

  @Around("""
      (@annotation(org.springframework.web.bind.annotation.RequestMapping)
       || @annotation(org.springframework.web.bind.annotation.GetMapping)
       || @annotation(org.springframework.web.bind.annotation.PostMapping)
       || @annotation(org.springframework.web.bind.annotation.PutMapping)
       || @annotation(org.springframework.web.bind.annotation.PatchMapping)
       || @annotation(org.springframework.web.bind.annotation.DeleteMapping))
       && execution(* com.yumi.*.infrastructure.web.*AdminController.*(..))
      """)
  public Object logAdminAction(ProceedingJoinPoint joinPoint) throws Throwable {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated())
      return joinPoint.proceed();

    String role = auth.getAuthorities().stream()
        .map(granted -> granted.getAuthority().replace("ROLE_", ""))
        .filter(ROLES_TO_LOG::contains)
        .findFirst()
        .orElse(null);
    if (role == null)
      return joinPoint.proceed();

    HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    String action = req.getMethod() + " " + req.getRequestURI();

    Object result = joinPoint.proceed();

    repository.save(AdminAuditLogMapper.toEntity(auth.getName(), role, action));

    return result;
  }
}