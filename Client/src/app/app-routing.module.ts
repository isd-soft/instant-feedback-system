// Modules
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
// Components
import { SignInComponent } from './sign-in/sign-in.component';
import { SignUpComponent } from './sign-up/sign-up.component';
import { EditPresentationComponent } from './edit-presentation/edit-presentation.component';
import { HomeComponent } from './home/home.component';
import { PresentationPageComponent } from './presentation-page/presentation-page.component';
import { UserProfileComponent } from './user-profile/user-profile.component';
// Services
import { PresentationDetailResolverService } from './services/presentation-detail-resolver.service';
import { UserDetailResolverService } from './services/user-detail-resolver.service';
import { PresentationListDetailResolverService } from './services/presentation-list-detail-resolver.service';
// Guards
import { AuthGuard } from './_guards/auth.guard';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    resolve: {
      presentations: PresentationListDetailResolverService
    }
  },
  { path: 'sign-in', component: SignInComponent },
  { path: 'sign-up', component: SignUpComponent },
  {
    path: 'new-presentation',
    component: EditPresentationComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'edit-presentation/:id',
    component: EditPresentationComponent,
    resolve: {
      presentation: PresentationDetailResolverService
    },
    canActivate: [AuthGuard]
  },
  {
    path: 'presentation-page/:id',
    component: PresentationPageComponent,
    resolve: {
      presentation: PresentationDetailResolverService
    }
  },
  {
    path: 'user-profile', component: UserProfileComponent,
    resolve: {
      user: UserDetailResolverService
    },
    canActivate: [AuthGuard]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
