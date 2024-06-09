package net.minecraft.src;

import net.lax1dude.eaglercraft.EaglerAdapter;
import net.lax1dude.eaglercraft.adapter.SimpleStorage;
import net.minecraft.client.Minecraft;

import java.util.List;

public class GuiTexturePacks extends GuiScreen {
	protected GuiScreen guiScreen;
	private int refreshTimer = -1;

	/** the absolute location of this texture pack */
	private String fileLocation = "";

	private boolean isSelectingPack = false;

	/**
	 * the GuiTexturePackSlot that contains all the texture packs and their
	 * descriptions
	 */
	private GuiTexturePackSlot guiTexturePackSlot;
	private GameSettings field_96146_n;

	public GuiTexturePacks(GuiScreen par1, GameSettings par2) {
		this.guiScreen = par1;
		this.field_96146_n = par2;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	public void initGui() {
		StringTranslate var1 = StringTranslate.getInstance();
		this.buttonList.add(new GuiSmallButton(5, this.width / 2 - 154, this.height - 48, var1.translateKey("texturePack.openFolder")));
		this.buttonList.add(new GuiSmallButton(6, this.width / 2 + 4, this.height - 48, var1.translateKey("gui.done")));
		this.mc.texturePackList.updateAvaliableTexturePacks();
		//this.fileLocation = (new File("texturepacks")).getAbsolutePath();
		this.guiTexturePackSlot = new GuiTexturePackSlot(this);
		this.guiTexturePackSlot.registerScrollButtons(this.buttonList, 7, 8);
	}

	/**
	 * Fired when a control is clicked. This is the equivalent of
	 * ActionListener.actionPerformed(ActionEvent e).
	 */
	protected void actionPerformed(GuiButton par1GuiButton) {
		if (par1GuiButton.enabled) {
			if (par1GuiButton.id == 5) {
				isSelectingPack = true;
				EaglerAdapter.openFileChooser("epk,.zip", null);
			} else if (par1GuiButton.id == 6) {
				// this.mc.renderEngine.refreshTextures();
				this.mc.displayGuiScreen(guiScreen);
			} else {
				this.guiTexturePackSlot.actionPerformed(par1GuiButton);
			}
		}
	}

	/**
	 * Called when the mouse is clicked.
	 */
	protected void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);
	}

	/**
	 * Called when the mouse is moved or a mouse button is released. Signature:
	 * (mouseX, mouseY, which) which==-1 is mouseMove, which==0 or which==1 is
	 * mouseUp
	 */
	protected void mouseMovedOrUp(int par1, int par2, int par3) {
		super.mouseMovedOrUp(par1, par2, par3);
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int par1, int par2, float par3) {
		this.guiTexturePackSlot.drawScreen(par1, par2, par3);

		if (this.refreshTimer <= 0) {
			this.mc.texturePackList.updateAvaliableTexturePacks();
			this.refreshTimer += 20;
		}

		StringTranslate var4 = StringTranslate.getInstance();
		this.drawCenteredString(this.fontRenderer, var4.translateKey("texturePack.title"), this.width / 2, 16, 16777215);
		this.drawCenteredString(this.fontRenderer, var4.translateKey("texturePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
		super.drawScreen(par1, par2, par3);
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen() {
		super.updateScreen();
		--this.refreshTimer;
		if (isSelectingPack && EaglerAdapter.getFileChooserResultAvailable()) {
			isSelectingPack = false;
			String name = EaglerAdapter.getFileChooserResultName();
			SimpleStorage.set(name.replaceAll("[^A-Za-z0-9_]", "_"), name.toLowerCase().endsWith(".zip") ? TexturePackList.zipToEpk(EaglerAdapter.getFileChooserResult()) : EaglerAdapter.getFileChooserResult());
			EaglerAdapter.clearFileChooserResult();
			this.mc.displayGuiScreen(this);
		}
	}

	@Override
	public void confirmClicked(boolean par1, int par2) {
		this.mc.displayGuiScreen(this);

		List var3 = this.mc.texturePackList.availableTexturePacks();

		if (par1) {
			SimpleStorage.set(((ITexturePack) var3.get(par2)).getTexturePackFileName(), null);
		} else {
			try {
				this.mc.texturePackList.setTexturePack((ITexturePack) var3.get(par2));
				this.mc.renderEngine.refreshTextures();
				this.mc.renderGlobal.loadRenderers();
			} catch (Exception var5) {
				var5.printStackTrace();
				this.mc.texturePackList.setTexturePack((ITexturePack) var3.get(0));
				this.mc.renderEngine.refreshTextures();
				this.mc.renderGlobal.loadRenderers();
				SimpleStorage.set(((ITexturePack) var3.get(par2)).getTexturePackFileName(), null);
			}
		}
	}
}
