# Aplicação de Meteorologia (Weather App)

Uma simples aplicação Android desenvolvida em Kotlin, em contexto académico que permite aos utilizadores consultar a meteorologia atual de qualquer cidade. A aplicação utiliza a API da OpenWeatherMap para obter os dados em tempo real e integra-se com o Firebase para autenticação de utilizadores e para guardar o histórico de pesquisas.

## Funcionalidades

*   **Pesquisa de Meteorologia:** Pesquise a temperatura e as condições climáticas de uma cidade à sua escolha.
*   **Feedback Visual:** A aplicação exibe um ícone e uma mensagem que se adaptam às condições do tempo (ex: ensolarado, nublado, chuvoso).
*   **Autenticação de Utilizadores:** Sistema de login e registo utilizando Firebase Authentication.
*   **Histórico de Pesquisas:** Cada pesquisa bem-sucedida é guardada no perfil do utilizador através do Cloud Firestore, permitindo um registo das cidades consultadas.

## Tecnologias Utilizadas

*   **Linguagem:** [Kotlin](https://kotlinlang.org/)
*   **Arquitetura:** Padrão de projeto simples com Activities
*   **Rede:**
    *   [Retrofit](https://square.github.io/retrofit/) para realizar chamadas HTTP à API.
    *   [Gson](https://github.com/google/gson) para fazer o parse dos dados JSON.
*   **Base de Dados e Autenticação:**
    *   [Firebase Authentication](https://firebase.google.com/docs/auth)
    *   [Cloud Firestore](https://firebase.google.com/docs/firestore)
*   **UI:**
    *   ViewBinding para uma interação segura com as Views.
    *   [Glide](https://github.com/bumptech/glide) para carregamento de imagens.
    *   [Lottie](https://airbnb.io/lottie/) para animações.

## Configuração do Projeto

Para executar este projeto localmente, siga os seguintes passos:

1.  **Clonar o repositório:**
    ```bash
    git clone https://github.com/seu-utilizador/seu-repositorio.git
    ```

2.  **Abrir no Android Studio:**
    *   Importe o projeto no Android Studio.

3.  **Configurar a Chave de API (OpenWeatherMap):**
    *   Crie uma conta no site da [OpenWeatherMap](https://openweathermap.org/api) e obtenha uma chave de API gratuita.
    *   Na raiz do projeto, crie um ficheiro chamado `local.properties`.
    *   Adicione a sua chave de API a este ficheiro, da seguinte forma:
      ```properties
      apiKey="SUA_CHAVE_DE_API_AQUI"
      ```

4.  **Configurar o Firebase:**
    *   Crie um projeto na [Firebase Console](https://console.firebase.google.com/).
    *   Adicione uma aplicação Android ao seu projeto Firebase.
    *   Siga os passos para descarregar o ficheiro `google-services.json`.
    *   Mova o ficheiro `google-services.json` para a pasta `app/` do seu projeto.
    *   Ative os serviços de **Authentication** (com o provedor de Email/Password) e **Firestore**.

5.  **Sincronizar e Executar:**
    *   Sincronize o projeto com os ficheiros Gradle e execute a aplicação num emulador ou dispositivo físico.
