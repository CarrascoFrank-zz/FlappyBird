package com.mygdx.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sun.org.apache.regexp.internal.RE;

import java.awt.Color;
import java.util.Random;

import static java.awt.Color.WHITE;

public class FlappyBird extends ApplicationAdapter {

	private SpriteBatch batch; // brach que vai instanciar a textura
    private Texture[] passaros; // onde vai ser passado a img do passaro
    private Texture fundo; //fundo do app
    private Texture canoBot;
    private  Texture canoTop;
    private Texture gameOver;
    private Random numeroRandomico;
    private BitmapFont fonte;
    private BitmapFont mensagem;
    private Circle passaroCirculo;
    private Rectangle retanguloTop;
    private Rectangle retanguloBot;

    //Camera
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 768;
    private final float VIRTUAL_HEIGHT = 1024;

    //Atributos de configuração

    private float larguraDispositivo;
    private float alturaDispositivo;
    private int estadoJogo = 0; // 0 -> jogo nao iniciado || 1 -> Jogo Iniciado ||  2 -> Game Over
    private int pontuacao = 0; //pontuação do jogo

    private float variacao =0;
    private float velocidadeQueda = 0;
    private float posicaoInicialVertical = 0;
    private float posicaoMovimentoCanoHorizontal;
    private float espacoEntreCanos;
    private  float deltaTime;
    private float alturaEntreCanosRandomica;
    private boolean marcouPonto = false;
    private ShapeRenderer shapeRenderer;


	public void create () {
        //Inicializando os elementos no app
        Gdx.app.log("Create", "Inicializando o jogo");
        batch = new SpriteBatch();
        numeroRandomico = new Random();
        passaroCirculo = new Circle();
        retanguloBot = new Rectangle();
        retanguloTop = new Rectangle();
        shapeRenderer =  new ShapeRenderer();

        fonte = new BitmapFont();
        mensagem = new BitmapFont();


        fonte.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        fonte.getData().setScale(4);
        mensagem.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        mensagem.getData().setScale(3);

        passaros = new Texture[3]; // array de texturas

        //Instanciando cada img no array com seu indice
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");

        //intanciando o fundo de tela
        fundo = new Texture("fundo.png");

        //instanciando os canos
        canoTop = new Texture("cano_topo.png");
        canoBot = new Texture("cano_baixo.png");

        //instanciando a tela de Game Over
        gameOver = new Texture("game_over.png");

        //Configuração da câmera
        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH/2,VIRTUAL_HEIGHT/2, 0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);


        larguraDispositivo = VIRTUAL_WIDTH;
        alturaDispositivo = VIRTUAL_HEIGHT;
        posicaoInicialVertical = alturaDispositivo/2;
        posicaoMovimentoCanoHorizontal = larguraDispositivo-100;
        espacoEntreCanos = 300;

	}

	@Override
	public void render () {

        camera.update();

        //limpando frames anteriores
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        deltaTime = Gdx.graphics.getDeltaTime();
        variacao += deltaTime * 10; //tempo para animação

        //resetando a variação para repetir a animação
        if (variacao > 2) {
            variacao = 0;
        }

        //Veirficaando o estado do jogo so para iniciar dps do primeiro toque
        if (estadoJogo == 0) {

            if (Gdx.input.justTouched()) {
                estadoJogo = 1;
            }

        }else {

            velocidadeQueda++; //queda do passaro
            //Limitação de queda
            if (posicaoInicialVertical > 10 || velocidadeQueda < 0) {
                posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda;
            }

            if (estadoJogo == 1) //Iniciado
            {
                posicaoMovimentoCanoHorizontal -= deltaTime * 100;//

                //Fazendo o passaro pular.
                if (Gdx.input.justTouched()) {
                    velocidadeQueda = -13;
                }

                //limitando e reseta o movimento do cano
                if (posicaoMovimentoCanoHorizontal < -canoTop.getWidth()) {
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
                    alturaEntreCanosRandomica = numeroRandomico.nextInt(400) - 200;
                    marcouPonto = false;
                }
                //verifica pontuação
                if (posicaoMovimentoCanoHorizontal < 120){
                    if (!marcouPonto) {
                        pontuacao++;
                        marcouPonto = true;
                    }
                }
            }else{ //tela de game over
                if (Gdx.input.justTouched()) {
                    estadoJogo = 0;
                    pontuacao = 0;
                    velocidadeQueda = 0;
                    posicaoInicialVertical = alturaDispositivo/2;
                    posicaoMovimentoCanoHorizontal = larguraDispositivo-200;
                }
            }
        }

        //Configurando dados de projeção da camera
        batch.setProjectionMatrix(camera.combined); //recuperando os dados de projeção.


        batch.begin();//inciando a exibição da textura

        batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo); //defininfo o prenchimento da tela com o fundo
        batch.draw(canoTop, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica); //desenhando o cano top na tela
        batch.draw(canoBot, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - canoBot.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica); //desenhando o cano bop na tela
        batch.draw(passaros[(int) variacao], 120, posicaoInicialVertical);
        fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo/2, alturaDispositivo-20);

        //somente quando colidir com o cano
        if (estadoJogo == 2){
            batch.draw(gameOver, larguraDispositivo/2 - gameOver.getWidth()/2,alturaDispositivo/2);
            mensagem.draw(batch,"Toque para reiniciar!", larguraDispositivo/2 - 200, alturaDispositivo/2 - gameOver.getHeight()/2);
        }
        batch.end();//Finalizando

        //Passaro
        passaroCirculo.set(120+passaros[0].getWidth()/2, posicaoInicialVertical+passaros[0].getHeight()/2,passaros[0].getWidth()/2);
        //cano baixo
        retanguloBot = new Rectangle(
                posicaoMovimentoCanoHorizontal, //posição x
                alturaDispositivo / 2 - canoBot.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica, //posição y
                canoBot.getWidth(), //largura do cano
                canoBot.getHeight() //altura do cano baixo

        );

        retanguloTop = new Rectangle(
                posicaoMovimentoCanoHorizontal,
                alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica,
                canoTop.getWidth(),
                canoTop.getHeight()
        );

        //desenhando formas
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//            shapeRenderer.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius); //desenhando o circulo
//            shapeRenderer.rect(retanguloBot.x, retanguloBot.y, retanguloBot.width, retanguloBot.height); //desenhando o retangulo
//            shapeRenderer.rect(retanguloTop.x,retanguloTop.y, retanguloTop.width, retanguloTop.height);
//            shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.RED);
//        shapeRenderer.end();


        //testando colisão[]
        if (Intersector.overlaps(passaroCirculo, retanguloBot) || Intersector.overlaps(passaroCirculo, retanguloTop) || (posicaoInicialVertical <= 0) || (posicaoInicialVertical >= alturaDispositivo)) {
            estadoJogo = 2;
            Gdx.app.log("Colisão", "Houve Colisão");
        }
	}

	//este metodo é chamado sempre que a largura do dispositivo é chamado.

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}


