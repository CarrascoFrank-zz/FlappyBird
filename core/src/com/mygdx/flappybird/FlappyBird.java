package com.mygdx.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FlappyBird extends ApplicationAdapter {

	private SpriteBatch batch; // brach que vai instanciar a textura
    private Texture[] passaros; // onde vai ser passado a img do passaro
    private Texture fundo; //fundo do app
    private Texture canoBot;
    private  Texture canoTop;



    //Atributos de configuração

    private int larguraDispositivo;
    private int alturaDispositivo;

    private float variacao =0;
    private float velocidadeQueda = 0;
    private  float posicaoInicialVertical = 0;
    private float posicaoMovimentoCanoHorizontal;
    private float espacoEntreCanos;


	public void create () {
        //Inicializando os elementos no app
        Gdx.app.log("Create", "Inicializando o jogo");
        batch = new SpriteBatch();
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

        espacoEntreCanos = 300;

        larguraDispositivo = Gdx.graphics.getWidth();
        alturaDispositivo = Gdx.graphics.getHeight();
        posicaoInicialVertical = alturaDispositivo/2;
        posicaoMovimentoCanoHorizontal = larguraDispositivo-100;

	}

	@Override
	public void render () {

        variacao += Gdx.graphics.getDeltaTime() * 10; //
        velocidadeQueda++;

        //resetando a variação para repetir a animação
        if (variacao>2){
            variacao = 0;
        }


        if(Gdx.input.justTouched()){
            //Gdx.app.log("Toque", "Toque na tela!");
            velocidadeQueda = -10;
        }

        //Limitação de queda
        if (posicaoInicialVertical > 10  || velocidadeQueda < 0) {
            posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda;
        }

        batch.begin();//inciando a exibição da textura

        batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo); //defininfo o prenchimento da tela com o fundo
        batch.draw(canoTop, posicaoMovimentoCanoHorizontal, alturaDispositivo/2 + espacoEntreCanos/2 ); //desenhando o cano top na tela
        batch.draw(canoBot, posicaoMovimentoCanoHorizontal, alturaDispositivo/2 - canoBot.getHeight() - espacoEntreCanos/2); //desenhando o cano bop na tela
        batch.draw(passaros[(int) variacao], 30, posicaoInicialVertical);

        batch.end();//Finalizando


	}

}
