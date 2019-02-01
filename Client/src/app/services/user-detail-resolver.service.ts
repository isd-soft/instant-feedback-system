import { Injectable } from '@angular/core';
import { UserDTO } from '../dto/user.dto';
import { UserService } from '../services/user.service';
import { Router, Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { take, mergeMap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class UserDetailResolverService implements Resolve<UserDTO> {

  constructor(private userServ: UserService, private router: Router) { }
  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<UserDTO> | Observable<never> {

    // const id = +route.paramMap.get('id');
    const id = JSON.parse(localStorage.getItem('userId'));
    return this.userServ.getUserById(id).pipe(
      take(1),
      mergeMap(user => {
        if (user) {
          return of(user);
        } else { // id not found
          this.router.navigate(['/sign-in']);
          return EMPTY;
        }
      })
    );
  }

}
