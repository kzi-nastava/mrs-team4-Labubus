import { HttpInterceptorFn } from '@angular/common/http';

export const interceptorInterceptor: HttpInterceptorFn = (req, next) => {
  const accessToken: any = localStorage.getItem('accessToken');
  //if (req.headers.get('skip')) return next(req);

  if (accessToken) {
    const cloned = req.clone({
      headers: req.headers.set('Authorization', 'Bearer ' + accessToken),
    });
    return next(cloned);
  } else {
    return next(req);
  }
};
