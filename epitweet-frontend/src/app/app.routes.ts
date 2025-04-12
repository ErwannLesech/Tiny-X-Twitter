import { Routes } from '@angular/router';
import { LandingPageComponent } from './pages/landing-page/landing-page.component';
import { LoginComponent } from './pages/login/login.component';
import { SignUpComponent } from './pages/sign-up/sign-up.component';
import { HomeComponent } from './pages/home/home.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { SearchResultsComponent } from './pages/search-results/search-results.component';
import { PostComponent } from './pages/post/post.component';

export const routes: Routes = [
  { path: '', redirectTo: 'landing', pathMatch: 'full' },
  { path: 'landing', component: LandingPageComponent },
  { path: 'login', component: LoginComponent },
  { path: 'sign-up', component: SignUpComponent },
  { path: 'home', component: HomeComponent },
  { path: 'profile/:userTag', component: ProfileComponent },
  { path: 'post/:postId', component: PostComponent },
  { path: 'search', component: SearchResultsComponent },
  { path: '**', redirectTo: 'landing' } // Redirect unknown paths to landing
];