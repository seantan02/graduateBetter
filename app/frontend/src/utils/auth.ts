import { API_URL } from './env.ts';

export async function isLoggedIn(){
    const accessToken = localStorage.getItem('accessToken');
    const refreshToken = localStorage.getItem('refreshToken');

    if(accessToken == undefined || refreshToken == undefined){
        return false;
    }

    const res = await fetch(`${API_URL}/student/me`, {
        method: "GET",
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`
        }
    });

    if([200, 201, 202].includes(res.status)){
        return true;
    }

    // try refreshing the token
    const resRefresh = await fetch(`${API_URL}/authentication/refresh-token`, {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            accessToken: accessToken,
            refreshToken: refreshToken
        })
    });

    if([200, 201, 202].includes(resRefresh.status)){
        const data = await resRefresh.json();

        localStorage.setItem('accessToken', data.accessToken);
        localStorage.setItem('refreshToken', data.refreshToken);
        return true;
    }

    return false;
}

export async function handleRequestVerification(email: string): Promise<boolean>{
    const res = await fetch(`${API_URL}/authentication/send-email`, {
        method: "POST",
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            email: email
        })
    });

    if([200, 201, 202].includes(res.status)){
        return true;
    }

    return false;
}

export async function handleVerification(email: string, code?: string): Promise<boolean>{
    if(code == undefined){
        return false;
    }

    const res = await fetch(`${API_URL}/authentication/verify-email`, {
        method: "POST",
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            email: email,
            code: code
        })
    });

    if([200, 201, 202].includes(res.status)){
        const data = await res.json();
        if(data != null){
            localStorage.setItem('accessToken', data.accessToken);
            localStorage.setItem('refreshToken', data.refreshToken);
            return true;
        }
    }

    return false;
}
