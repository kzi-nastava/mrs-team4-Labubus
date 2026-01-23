import { HttpInterceptorFn } from '@angular/common/http';

export const interceptorInterceptor: HttpInterceptorFn = (req, next) => {
  const accessToken: any = localStorage.getItem('accessToken');
  //if (req.headers.get('skip')) return next(req);

  if (req.url.includes('routing.openstreetmap.de') || req.url.includes('router.project-osrm.org') || req.url.includes('nominatim.openstreetmap.org')) {
    return next(req);
  }

  if (accessToken) {
    const cloned = req.clone({
      headers: req.headers.set('Authorization', 'Bearer ' + accessToken),
    });
    return next(cloned);
  } else {
    return next(req);
  }
};
